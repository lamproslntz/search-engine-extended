import cleaner.TextCleaner;

import index.Indexer;
import index.Searcher;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * @author Lampros Lountzis
 */
public class SearchEngine {
    public static void main(String[] args) {
        // read documents
        System.out.println("[INFO] SearchEngine.main - Reading documents...");
        List<Map<String, String>> docs = readCISIDocuments("C:\\Users\\lampr\\Downloads\\cisi\\CISI.ALL");
        if (docs == null) {
            System.exit(1);
        }

        // read queries
        System.out.println("[INFO] SearchEngine.main - Reading queries...");
        List<Map<String, String>> queries = readCISIQueries("C:\\Users\\lampr\\Downloads\\cisi\\CISI.QRY");
        if (queries == null) {
            System.exit(1);
        }

        TextCleaner cleaner = new TextCleaner(true, true);

        // process documents
        cleaner.clean(docs, new String[]{"title", "abstract"});

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

        try {
            searcher.search(queries, 10);
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

    }

    /**
     * Read CISI dataset documents. <br>
     * The documents are of the form: (ID, title, author, abstract). The cross-references section is skipped.
     *
     * @param path path of CISI documents file.
     *
     * @return a list of documents.
     */
    public static List<Map<String, String>> readCISIDocuments(String path) {
        Path file = Paths.get(path);
        if (Files.isDirectory(file)) { // check if path links to a directory
            System.err.println("[ERROR] file.FileHandler.readCISIDocuments - " + path + " is a directory, not a file.");
            return null;
        } else if (!Files.exists(file)) { // check if file exists
            System.err.println("[ERROR] file.FileHandler.readCISIDocuments - " + path + " doesn't exist.");
            return null;
        } else if (!FilenameUtils.getExtension(path).equals("ALL")) { // check if file extensions isn't .ALL
            System.err.println("[ERROR] file.FileHandler.readCISIDocuments - invalid file extension (required .ALL).");
            return null;
        }

        List<Map<String, String>> docs = new ArrayList<>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
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

            scanner.close();
        } catch (IOException e) {
            System.err.println("[ERROR] file.FileHandler.readCISIDocuments - Problem occurred while reading the documents.");
            e.printStackTrace();

            if (scanner != null) {
                scanner.close();
            }

            return null;
        }

        return docs;
    }

    /**
     * Read CISI dataset queries. <br>
     * The queries are of the form: (ID, text).
     *
     * @param path path of CISI queries file.
     *
     * @return a list of queries.
     */
    public static List<Map<String, String>> readCISIQueries(String path) {
        Path file = Paths.get(path);
        if (Files.isDirectory(file)) { // check if path links to a directory
            System.err.println("[ERROR] file.FileHandler.readCISIQueries - " + path + " is a directory, not a file.");
            return null;
        } else if (!Files.exists(file)) { // check if file exists
            System.err.println("[ERROR] file.FileHandler.readCISIQueries - " + path + " doesn't exist.");
            return null;
        } else if (!FilenameUtils.getExtension(path).equals("QRY")) { // check if file extensions isn't .QRY
            System.err.println("[ERROR] file.FileHandler.readCISIQueries - invalid file extension (required .QRY).");
            return null;
        }

        List<Map<String, String>> queries = new ArrayList<>();

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
            scanner.useDelimiter("\\.I"); // separate each query using the query ID (.I flag)

            // queries are dictionaries with the following fields: (ID, text)
            Map<String, String> query;
            String text;
            while (scanner.hasNext()) {
                text = scanner.next();

                query = new HashMap<>();

                // read query ID
                // query ID is between .I and .W flags
                String queryID = text.substring(0, text.indexOf(".W")).trim();
                query.put("id", queryID);

                // read query text
                // query text is between .W and next query
                String queryText = text.substring(text.indexOf(".W") + 2).trim();
                query.put("text", queryText);

                queries.add(query);
            }

            scanner.close();
        } catch (IOException e) {
            System.err.println("[ERROR] file.FileHandler.readCISIQueries - Problem occurred while reading the queries.");
            e.printStackTrace();

            if (scanner != null) {
                scanner.close();
            }

            return null;
        }

        return queries;
    }
}
