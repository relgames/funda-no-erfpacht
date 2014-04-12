package org.relgames.funda.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class NoErfpacht {
    private static final Logger log = LoggerFactory.getLogger(NoErfpacht.class);

    private static final String FUNDA_URL = "http://m.funda.nl/koop/amsterdam/200000-375000/85+woonopp/";

    public static void main(String[] args) throws IOException {
        Document document = Jsoup.connect(FUNDA_URL).get();

        List<Element> elements = document.select("a.prop-item");
        log.info("Loaded {} elements", elements.size());

        for (Element element : elements) {
            String url = "http://m.funda.nl" + element.attr("href");
            String situation = groundSituation(url);
            System.out.println(url + "," + situation);
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

        return null;
    }
}
