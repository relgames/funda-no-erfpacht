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

public class NoErfpacht {
    private static final Logger log = LoggerFactory.getLogger(NoErfpacht.class);

    private static final String FUNDA_URL = "http://m.funda.nl/koop/amsterdam/200000-375000/85+woonopp/bouwperiode-1981-1990/bouwperiode-1991-2000/bouwperiode-2001-2010/bouwperiode-na-2010/";
    private static final String CACHE_FILE = "cache/cache.dat";

    public static void main(String[] args) throws IOException {
        final String url;
        if (args.length==1) {
            url = args[0];
        } else {
            url = FUNDA_URL;
        }

        RecordManager recMan = RecordManagerFactory.createRecordManager(CACHE_FILE);
        ExecutorService executorService = Executors.newFixedThreadPool(8);

        System.out.println("<html><body><table>");

        try {
            final Map<String, String> cache = recMan.hashMap("erfpacht");
            List<Future<?>> futures = new ArrayList<>();

            for (int i=1;; i++) {
                String fundaUrl = url + "p" + i;
                log.info("Processing {}", fundaUrl);
                Document document = Jsoup.connect(fundaUrl).get();

                List<Element> elements = document.select("a.prop-item");
                if (elements.isEmpty()) {
                    break;
                }
                log.debug("Loaded {} elements", elements.size());

                for (Element element : elements) {
                    final String houseUrl = "http://m.funda.nl" + element.attr("href");
                    futures.add(executorService.submit(new Runnable() {
                        @Override
                        public void run() {
                            String situation = cache.get(houseUrl);
                            if (situation == null) {
                                situation = groundSituation(houseUrl);
                                cache.put(houseUrl, situation);
                            }
                            String url2 = houseUrl.replace("http://m.", "http://");
                            if (situation.toLowerCase().contains("volle eigendom")) {
                                System.out.println(String.format("<tr><td><a target='_blank' href='%s'>%s</a></td><td>%s</td></tr>", url2, url2, situation));
                            }
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

            System.out.println("</table></body></html>");
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
