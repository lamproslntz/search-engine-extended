package index;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Lampros Lountzis
 */
public interface SearcherInterface {

    Map<String, List<Pair<Document, Float>>> search(List<Map<String, String>> queries, int k) throws IOException, ParseException;

    void open() throws IOException;

    void close() throws IOException;

}
