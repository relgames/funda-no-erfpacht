package org.relgames.funda.parser;

/**
 * @author opoleshuk
 */
public enum OutputFormat {
    HTML(new HtmlWriter());
    //CSV(null);

    private OutputFormat(HouseWriter writer) {
        this.writer = writer;
    }

    private final HouseWriter writer;

    public HouseWriter getWriter() {
        return writer;
    }
}
