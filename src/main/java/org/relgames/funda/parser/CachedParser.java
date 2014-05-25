//package org.relgames.funda.parser;
//
//import jdbm.RecordManager;
//import jdbm.RecordManagerFactory;
//
//import java.io.IOException;
//import java.util.Map;
//
///**
// * @author opoleshuk
// */
//public class CachedParser implements Parser {
//    private static final String CACHE_FILE = "cache/cache.dat";
//
//    private final Parser parser;
//    private final RecordManager recMan;
//    private final Map<String, String> cache;
//
//
//    public CachedParser(Parser parser) throws IOException {
//        recMan = RecordManagerFactory.createRecordManager(CACHE_FILE);
//        cache = recMan.hashMap("erfpacht");
//
//        this.parser = parser;
//    }
//
//    @Override
//    public Model parse(String url) throws IOException {
//        String situation = cache.get(url);
//        if (situation == null) {
//            situation = groundSituation(houseUrl);
//            cache.put(houseUrl, situation);
//        }
//
//
//        recMan.commit();
//    }
//}
