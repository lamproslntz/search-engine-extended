package com.lamproslntz.searchengineextended.index;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * @author Lampros Lountzis
 */
public class Indexer implements IndexerInterface {

    private final String INDEX_DIR;
    private IndexWriter writer;

    public Indexer(String indexDir) {
        this.INDEX_DIR = indexDir;
    }

    public void create() throws IOException {
        // create directory in file system for index
        Directory dir = FSDirectory.open(Paths.get(INDEX_DIR));

        // analyzer for the normalization of documents
        Analyzer analyzer = new EnglishAnalyzer();

        // similarity function for document-query similarity and scoring
        Similarity similarity = new BM25Similarity();

        // configure index writer with similarity function, analyzer and creation mode
        // create a new index in the directory, removing any previously indexed documents
        IndexWriterConfig writerConfig = new IndexWriterConfig(analyzer);
        writerConfig.setSimilarity(similarity);
        writerConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        // create the index writer with the configurations
        writer = new IndexWriter(dir, writerConfig);
    }

    public void close() throws IOException {
        writer.close();
    }

    public void index(List<Map<String, String>> docs) throws IOException {
        for (Map<String, String> doc : docs) {
            Document luceneDoc = new Document();

            // create the fields of the doc and add them to the doc object
            // the fields of each document are (ID, title, title_norm, author, abstract, abstract_norm)
            luceneDoc.add(new StoredField("id", doc.get("id"))); // not indexed, just stored for retrieval
            luceneDoc.add(new StoredField("title", doc.get("title"))); //  not indexed, just stored for retrieval
            luceneDoc.add(new TextField("title_norm", doc.get("title"), Field.Store.NO)); // indexed, analyzed, not stored
            luceneDoc.add(new StoredField("author", doc.get("author"))); // not indexed, just stored for retrieval
            luceneDoc.add(new StoredField("abstract", doc.get("abstract"))); //  not indexed, just stored for retrieval
            luceneDoc.add(new TextField("abstract_norm", doc.get("abstract"), Field.Store.NO)); // indexed, analyzed, not stored

            writer.addDocument(luceneDoc);
        }
    }

    public String getIndexDirectory() {
        return INDEX_DIR;
    }
}
