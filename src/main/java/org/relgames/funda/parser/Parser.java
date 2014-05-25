package org.relgames.funda.parser;

import java.io.IOException;

/**
 * @author opoleshuk
 */
public interface Parser {
    House parse(String url) throws IOException;
}
