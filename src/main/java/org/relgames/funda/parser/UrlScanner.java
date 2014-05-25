package org.relgames.funda.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author opoleshuk
 */
public class UrlScanner {
    private static final Logger log = LoggerFactory.getLogger(UrlScanner.class);

    private final Parser parser;
    private final ExecutorService executorService = Executors.newFixedThreadPool(8);


    public UrlScanner(Parser parser) {
        this.parser = parser;
    }


    public List<House> scan(String url) throws IOException {
        List<Future<House>> futures = new ArrayList<>();

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
                futures.add(executorService.submit(new Callable<House>() {
                    @Override
                    public House call() throws Exception {
                        return parser.parse(houseUrl);
                    }
                }));
            }
        }

        List<House> result = new ArrayList<>();
        log.info("Waiting...");
        for (Future<House> future : futures) {
            try {
                result.add(future.get());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        log.info("Done: processed {} url", futures.size());
        return result;
    }

}
