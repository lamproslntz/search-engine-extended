import cleaner.TextCleaner;
import index.Indexer;
import index.Searcher;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Lampros Lountzis
 */
public class SearchEngine {

    private static final String qrels = "qrels.txt";

    public static void main(String[] args) {
        // read documents
        System.out.println("[INFO] SearchEngine.main - Reading documents...");
        List<Map<String, String>> docs = readCISIDocuments("C:\\Users\\lampr\\Downloads\\cisi\\CISI.ALL");

        // read queries
        System.out.println("[INFO] SearchEngine.main - Reading queries...");
        List<Map<String, String>> queries = readCISIQueries("C:\\Users\\lampr\\Downloads\\cisi\\CISI.QRY");

        TextCleaner cleaner = new TextCleaner(false, true);
        // process documents
        cleaner.clean(docs, new String[]{"title", "author", "abstract"});
        // process queries
        cleaner.clean(queries, new String[]{"text"});

        Indexer indexer = new Indexer("C:\\Users\\lampr\\Downloads\\index");

        try {
            indexer.create();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            indexer.index(docs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            indexer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Searcher searcher = new Searcher("C:\\Users\\lampr\\Downloads\\index");

        try {
            searcher.open();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, List<Pair<Document, Float>>> results = null;
        try {
            results = searcher.search(queries, 10);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        try {
            searcher.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        boolean flag = writeResults(results, "C:\\Users\\lampr\\Downloads\\cisi\\results.txt");

    }

    /**
     * Read CISI dataset documents. <br>
     * The documents are of the form: (ID, title, author, abstract). The cross-references section is skipped.
     *
     * @param path CISI documents file path.
     *
     * @return a list of documents.
     */
    private static List<Map<String, String>> readCISIDocuments(String path) {
        Path file = Paths.get(path);
        if (Files.isDirectory(file)) { // check if path links to a directory
            System.err.println("[ERROR] readCISIDocuments - " + path + " is a directory, not a file.");
            return null;
        } else if (!Files.exists(file)) { // check if file exists
            System.err.println("[ERROR] readCISIDocuments - " + path + " doesn't exist.");
            return null;
        } else if (!FilenameUtils.getExtension(path).equals("ALL")) { // check if file extensions isn't .ALL
            System.err.println("[ERROR] readCISIDocuments - invalid file extension (required .ALL).");
            return null;
        }

        List<Map<String, String>> docs = new ArrayList<>();

        try (Scanner scanner = new Scanner(file)) {
            scanner.useDelimiter("\\.I "); // separate each document using the doc ID (.I flag)

            // docs are dictionaries with the following fields: (ID, title, author, abstract)
            Map<String, String> doc;
            String text;
            while (scanner.hasNext()) {
                text = scanner.next();

                doc = new HashMap<>();

                // read doc ID
                // doc ID is between .I and .T flags
                String docID = text.substring(0, text.indexOf(".T")).trim();
                doc.put("id", docID);

                // read doc title
                // doc title is between .T and .A flags
                String docTitle = text.substring(text.indexOf(".T") + 2, text.indexOf(".A")).trim();
                doc.put("title", docTitle);

                // read doc author
                // doc author is between .A and .W flags
                String docAuthor = text.substring(text.indexOf(".A") + 2, text.indexOf(".W")).trim();
                doc.put("author", docAuthor);

                // read doc abstract
                // doc abstract is between .W and .X flags
                String docAbstract = text.substring(text.indexOf(".W") + 2, text.indexOf(".X")).trim();
                doc.put("abstract", docAbstract);

                docs.add(doc);
            }

        } catch (IOException e) {
            System.err.println("[ERROR] readCISIDocuments - Problem occurred while reading the documents.");
            e.printStackTrace();
            return null;
        }

        return docs;
    }

    /**
     * Read CISI dataset queries. <br>
     * The queries are of the form: (ID, text).
     *
     * @param path CISI queries file path.
     *
     * @return a list of queries.
     */
    private static List<Map<String, String>> readCISIQueries(String path) {
        Path file = Paths.get(path);
        if (Files.isDirectory(file)) { // check if path links to a directory
            System.err.println("[ERROR] readCISIQueries - " + path + " is a directory, not a file.");
            return null;
        } else if (!Files.exists(file)) { // check if file exists
            System.err.println("[ERROR] readCISIQueries - " + path + " doesn't exist.");
            return null;
        } else if (!FilenameUtils.getExtension(path).equals("QRY")) { // check if file extensions isn't .QRY
            System.err.println("[ERROR] readCISIQueries - invalid file extension (required .QRY).");
            return null;
        }

        List<Map<String, String>> queries = new ArrayList<>();

        try (Scanner scanner = new Scanner(file);) {
            scanner.useDelimiter("\\.I"); // separate each query using the query ID (.I flag)

            // queries are dictionaries with the following fields: (ID, text)
            Map<String, String> query;
            String text;
            while (scanner.hasNext()) {
                text = scanner.next();

                query = new HashMap<>();

                // read query ID
                // query ID is either between .I and .W flags or .I and .T flags
                String queryID;
                if (text.contains(".T")) {
                    queryID = text.substring(0, text.indexOf(".T")).trim();
                } else {
                    queryID = text.substring(0, text.indexOf(".W")).trim();
                }
                query.put("id", queryID);

                // read query text
                // query text is either between .W and next query or .W and .B flags
                String queryText;
                if (text.contains(".B")) {
                    queryText = text.substring(text.indexOf(".W") + 2, text.lastIndexOf(".B")).trim();
                } else {
                    queryText = text.substring(text.indexOf(".W") + 2).trim();
                }
                query.put("text", queryText);

                queries.add(query);
            }

        } catch (IOException e) {
            System.err.println("[ERROR] readCISIQueries - Problem occurred while reading the queries.");
            e.printStackTrace();
            return null;
        }

        return queries;
    }

    /**
     * Creates query relevance file (qrels.txt) according to trec_eval specifications.
     * The qrels.txt file has records of the form: (query_id, iteration, doc_id, relevance).
     *
     * @param relPath CISI query relevance file path.
     * @param filePath directory to save qrels.txt
     *
     * @return true if operation was successful, otherwise, false.
     */
    private static boolean createRelevanceFile(String relPath, String filePath) {
        Path relFile = Paths.get(relPath);
        if (Files.isDirectory(relFile)) { // check if path links to a directory
            System.err.println("[ERROR] createRelevantFile - " + relPath + " is a directory, not a file.");
            return false;
        } else if (!Files.exists(relFile)) { // check if file exists
            System.err.println("[ERROR] createRelevantFile - " + relPath + " doesn't exist.");
            return false;
        } else if (!FilenameUtils.getExtension(relPath).equals("REL")) { // check if file extensions isn't .ALL
            System.err.println("[ERROR] createRelevantFile - invalid file extension (required .REL).");
            return false;
        }

        Path outDir = Paths.get(filePath);
        if (!Files.isDirectory(outDir)) { // check if path links to an existing directory
            System.err.println("[ERROR] createRelevantFile - " + filePath + " isn't a directory.");
            return false;
        }


        try (BufferedReader reader = Files.newBufferedReader(relFile);
             BufferedWriter writer = Files.newBufferedWriter(Paths.get(filePath, qrels))) {

            String line;
            String[] tokens;
            String record;
            while ((line = reader.readLine()) != null) {
                // split each line on whitespace and/or tab
                tokens = line.trim().split("\s+|\t+");
                // qrels.txt records are of the form: (query_id, iteration, doc_id, relevance)
                record = tokens[0] + " " + tokens[2] + " " + tokens[1] + " " + "1";

                writer.write(record);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("[ERROR] createRelevantFile - problem occurred while reading/writing.");
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * Write search results according to trec_eval specifications.
     * The results file has records of the form: (query_id, iteration, doc_id, rank, similarity, run_id).
     *
     * @param results a dictionary of the form {key: query_id, value: list of pairs (doc_id, similarity_score)}.
     * @param path results file path.
     *
     * @return true if operation was successful, otherwise, false.
     */
    private static boolean writeResults(Map<String, List<Pair<Document, Float>>> results, String path) {
        if (results == null || results.isEmpty()) { // check if results is null or empty
            System.err.println("[ERROR] writeResults - empty results set.");
            return false;
        }

        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(path))) {
            String record;
            for (String queryID : results.keySet()) {
                for (Pair<Document, Float> pair : results.get(queryID)) {
                    // results file contains records of the form: (query_id, iteration, doc_id, rank, similarity, run_id)
                    record = queryID + " 0 " + pair.getKey().getField("id").stringValue() + " 0 " + pair.getValue() + " IRmodel";
                    writer.write(record);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("[ERROR] writeResults - problem occurred while writing results to " + path + ".");
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
