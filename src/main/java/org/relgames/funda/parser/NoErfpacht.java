package org.relgames.funda.parser;

import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class NoErfpacht {
    private static final Logger log = LoggerFactory.getLogger(NoErfpacht.class);

    private static final String FUNDA_URL = "http://m.funda.nl/koop/amsterdam/200000-375000/85+woonopp/";
    private static final String CACHE_FILE = "cache.dat";

    public static void main(String[] args) throws IOException {
        RecordManager recMan = RecordManagerFactory.createRecordManager(CACHE_FILE);
        try {
            Map<String, String> cache = recMan.hashMap("erfpacht");

            Document document = Jsoup.connect(FUNDA_URL).get();

            List<Element> elements = document.select("a.prop-item");
            log.info("Loaded {} elements", elements.size());

            for (Element element : elements) {
                String url = "http://m.funda.nl" + element.attr("href");
                String situation = cache.get(url);
                if (situation == null) {
                    situation = groundSituation(url);
                    cache.put(url, situation);
                }
                System.out.println(url + "," + situation);
            }
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
