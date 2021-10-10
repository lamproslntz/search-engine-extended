package com.lamproslntz.searchengineextended.index;

import com.lamproslntz.searchengineextended.analyzer.Word2VecSynonymAnalyzer;
import com.lamproslntz.searchengineextended.dto.DocumentDTO;
import com.lamproslntz.searchengineextended.dto.QueryDTO;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
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

import org.deeplearning4j.models.word2vec.Word2Vec;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Searcher module, that searches a Lucene index given a user query. The searcher queries the normalized
 * title and abstract fields with the help of {@link MultiFieldQueryParser} and {@link BM25Similarity} is used for
 * document-query similarity. During query time, the query terms are expanded with their synonyms, based on word
 * embeddings, using {@link Word2VecSynonymAnalyzer}.
 *
 * @author Lampros Lountzis
 */
public class Searcher implements SearcherInterface {

    private final String INDEX_DIR;
    private IndexReader reader;

    private final Word2Vec MODEL;
    private final double MIN_ACCURACY;

    /**
     * Initializes a Searcher.
     *
     * @param indexDir the directory path where the Lucene index files are hosted.
     * @param model Word2Vec model.
     * @param minAccuracy word similarity minimum accuracy for Word2Vec model.
     */
    public Searcher(String indexDir, Word2Vec model, double minAccuracy) {
        this.INDEX_DIR = indexDir;
        this.MODEL = model;
        this.MIN_ACCURACY = minAccuracy;
    }

    /**
     * Searches a Lucene index.
     * The document look-up is done using the title and the abstract normalized fields with the help of
     * {@link MultiFieldQueryParser}, {@link Word2VecSynonymAnalyzer} is used for query analysis
     * (query terms are expanded with their synonyms based on word embeddings), {@link BM25Similarity} is
     * used for document-query similarity.
     *
     * @param userQuery the user's query.
     * @param k number of top documents to be retrieved.
     *
     * @return list of top k retrieved documents, with respect to the user's query.
     *
     * @throws IOException if the Lucene index cannot be searched.
     * @throws ParseException if the user's query cannot be parsed.
     */
    public List<DocumentDTO> search(QueryDTO userQuery, int k) throws IOException, ParseException {
        String[] fields = {"title_norm", "abstract_norm"}; // the searchable fields

        if (reader != null) {
            // analyzer used for the normalization of the query
            Analyzer analyzer = new Word2VecSynonymAnalyzer(MODEL, MIN_ACCURACY);

            // create a searcher for searching the index, and configure it
            IndexSearcher searcher = new IndexSearcher(reader);
            searcher.setSimilarity(new BM25Similarity());

            // create a query parser on the searchable field
            QueryParser parser = new MultiFieldQueryParser(fields, analyzer);

            // parse the query (query is a dictionary with (ID, text))
            Query query = parser.parse(userQuery.getQuery());
            // results are of the form: [(doc, score), (doc, score), ...]
            List<DocumentDTO> results = new ArrayList<>();
            // hits returned by searching the index
            TopDocs hits = searcher.search(query, k);
            for (ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = searcher.doc(scoreDoc.doc);
                results.add(new DocumentDTO(doc, scoreDoc.score));
            }

            return results;
        }

        return null;
    }

    /**
     * Opens the Lucene index to be used by this Searcher.
     *
     * @throws IOException if the Lucene index cannot be opened.
     */
    public void open() throws IOException {
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));
        reader = DirectoryReader.open(dir);
    }

    /**
     * Frees persistent resources used by this Searcher.
     *
     * @throws IOException if the Searcher is closed.
     */
    public void close() throws IOException {
        reader.close();
    }

    /**
     * @return the directory path where the Lucene index files are hosted.
     */
    public String getIndexDirectory() {
        return INDEX_DIR;
    }

    /**
     * @return Word2Vec model.
     */
    public Word2Vec getModel() {
        return MODEL;
    }

    /**
     * @return word similarity minimum accuracy for Word2Vec model.
     */
    public double getMinAccuracy() {
        return MIN_ACCURACY;
    }
}
