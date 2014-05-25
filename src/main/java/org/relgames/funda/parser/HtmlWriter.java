package org.relgames.funda.parser;

import java.io.PrintWriter;
import java.util.List;

/**
 * @author opoleshuk
 */
public class HtmlWriter implements HouseWriter {
    @Override
    public void write(PrintWriter writer, List<House> houses) {
        writer.println("<html><body><table>");

        for (House house : houses) {
            String url2 = house.getUrl().replace("http://m.", "http://");
            writer.println(String.format("<tr><td><a target='_blank' href='%s'>%s</a></td><td>%s</td></tr>", url2, url2, house.getErfpacht()));
        }

        writer.println("</table></body></html>");
    }
}
