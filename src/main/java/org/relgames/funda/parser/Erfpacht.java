package org.relgames.funda.parser;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Erfpacht {
    private static final Logger log = LoggerFactory.getLogger(Erfpacht.class);

    private static final String FUNDA_URL = "http://m.funda.nl/koop/amsterdam/200000-375000/85+woonopp/";
    private static final String CACHE_FILE = "cache/cache.dat";

    public static void main(String[] args) throws IOException {
        RecordManager recMan = RecordManagerFactory.createRecordManager(CACHE_FILE);
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        try {
            final Map<String, String> cache = recMan.hashMap("erfpacht");
            List<Future<?>> futures = new ArrayList<>();

            for (int i=1;; i++) {
                String fundaUrl = FUNDA_URL + "p" + i;
                log.info("Processing {}", fundaUrl);
                Document document = Jsoup.connect(fundaUrl).get();

                List<Element> elements = document.select("a.prop-item");
                if (elements.isEmpty()) {
                    break;
                }
                log.debug("Loaded {} elements", elements.size());

                for (Element element : elements) {
                    final String url = "http://m.funda.nl" + element.attr("href");
                    futures.add(executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            String situation = cache.get(url);
                            if (situation == null) {
                                situation = groundSituation(url);
                                cache.put(url, situation);
                            }
                            System.out.println(url + "," + situation);
                        }
                    }));
                }
            }

            log.info("Waiting...");
            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    log.warn("Error, ignoring", e);
                }
            }
            log.info("Done: processed {} url", futures.size());
            executorService.shutdown();
        } finally {
            recMan.commit();
        }
    }

    private static String groundSituation(String url) {
        log.debug("Checking {}", url);
        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "kenmerken/";

        try {
            Document document = Jsoup.connect(url).get();
            for (Element element : document.select("tr.sub-cat")) {
                String caption = element.getElementsByTag("th").get(0).text();
                if (caption.contains("Eigendomssituatie")) {
                    return element.getElementsByClass("specs-val").text();
                }
            }

        } catch (IOException e) {
            log.error("Can't process {}, ignoring", e);
        }

        return "";
    }
}
