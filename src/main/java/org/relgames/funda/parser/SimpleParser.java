package org.relgames.funda.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author opoleshuk
 */
public class SimpleParser implements Parser {
    private static final Logger log = LoggerFactory.getLogger(SimpleParser.class);

    @Override
    public House parse(String url) throws IOException {
        log.debug("Checking {}", url);

        if (!url.endsWith("/")) {
            url += "/";
        }
        url += "kenmerken/";

        Document document = Jsoup.connect(url).get();
        House result = new House();
        for (Element element : document.select("tr.sub-cat")) {
            String caption = element.getElementsByTag("th").get(0).text();

            if (caption.contains("Eigendomssituatie")) {
                result.setErfpacht(element.getElementsByClass("specs-val").text());
            }
        }

        return result;
    }
}
