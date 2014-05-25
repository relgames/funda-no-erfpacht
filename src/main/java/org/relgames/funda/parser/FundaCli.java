package org.relgames.funda.parser;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author opoleshuk
 */
public class FundaCli {

    public static class Options {
        @Option(name = "-u", usage = "URL", required = true, metaVar = "URL")
        private String url;

        @Option(name = "-o", usage = "html/csv (default html)")
        private OutputFormat outputFormat = OutputFormat.HTML;

        @Option(name = "-f", usage = "filter (default ve): \n" +
                "ve to output only \"volle eigendom\" houses\n" +
                "ne to output houses which do not mention erfpacht")
        private HouseFilter houseFilter = HouseFilter.VE;

    }


    public static void main(String[] args) throws IOException {
        Options options = parseOptions(args);

        Parser parser = new Repeater(new SimpleParser(), 10);
        UrlScanner urlScanner = new UrlScanner(parser);

        List<House> houses = urlScanner.scan(options.url);
        houses = options.houseFilter.filter(houses);

        HouseWriter writer = options.outputFormat.getWriter();
        writer.write(new PrintWriter(System.out), houses);
    }

    private static Options parseOptions(String[] args) {
        Options options = new Options();
        CmdLineParser cmdLineParser = new CmdLineParser(options);
        try {
            cmdLineParser.parseArgument(args);
        } catch (CmdLineException e) {
            System.err.println(e.getMessage());
            System.err.println("java -jar funda.jar [options...] arguments...");
            cmdLineParser.printUsage(System.err);
            System.exit(-1);
        }

        return options;
    }
}
