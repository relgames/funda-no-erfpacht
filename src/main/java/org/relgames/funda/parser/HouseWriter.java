package org.relgames.funda.parser;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.List;

/**
 * @author opoleshuk
 */
public interface HouseWriter {
    void write(PrintWriter writer, List<House> houses);
}
