package index;

import analyzers.Word2VecSynonymAnalyzer;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lampros Lountzis
 */
public class Searcher implements SearcherInterface {

    private final String indexDir;
    private IndexReader reader;

    public Searcher(String indexDir) {
        this.indexDir = indexDir;
    }

    public Map<String, List<Pair<Document, Float>>> search(List<Map<String, String>> queries, int k) throws IOException, ParseException {
        String[] fields = {"title", "abstract"}; // the searchable fields

        if (reader != null) {
            // analyzer used for the normalization of the query
            Analyzer analyzer = new Word2VecSynonymAnalyzer();

            // create a searcher for searching the index, and configure it
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new BM25Similarity());

            // create a query parser on the searchable field
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);

            Query query;
            // results are of the form: (queryID, [(doc, score), (doc, score), ...])
            Map<String, List<Pair<Document, Float>>> results = new HashMap<>();
            for (Map<String, String> q : queries) {
                // parse the query (query is a dictionary with (ID, text))
                query = parser.parse(q.get("text"));

                // hits returned by searching the index
                TopDocs hits = searcher.search(query, k);
                // create a slot for the query results
                results.put(q.get("id"), new ArrayList<>());
                for (ScoreDoc scoreDoc : hits.scoreDocs) {
                    Document doc = searcher.doc(scoreDoc.doc);
                    results.get(q.get("id")).add(new MutablePair<>(doc, scoreDoc.score));
                }

            }

            return results;
        }

        return null;
    }

    public void open() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(indexDir));
        reader = DirectoryReader.open(dir);
    }

    public void close() throws IOException {
        reader.close();
    }
}
