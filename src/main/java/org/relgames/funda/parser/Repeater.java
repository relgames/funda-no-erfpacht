package org.relgames.funda.parser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author opoleshuk
 */
public class Repeater implements Parser {
    private static final Logger log = LoggerFactory.getLogger(Repeater.class);

    private final int maxTries;
    private final Parser parser;

    public Repeater(Parser parser, int maxTries) {
        this.parser = parser;
        this.maxTries = maxTries;
    }

    @Override
    public House parse(String url) throws IOException {
        for (int tryCount = 1; tryCount <= maxTries; tryCount++ ) {
            try {
                return parser.parse(url);
            } catch (Exception e) {
                log.warn("Request #{} to {} failed", tryCount, url, e);
            }
        }

        throw new RuntimeException("Tried url "+url+" " + maxTries + " times, failed");
    }
}
