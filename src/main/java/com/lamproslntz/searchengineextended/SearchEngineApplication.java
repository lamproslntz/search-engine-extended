package com.lamproslntz.searchengineextended;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Lampros Lountzis
 */
@SpringBootApplication
public class SearchEngineApplication {

    public static void main(String[] args) {
        // initialize web application
        SpringApplication.run(SearchEngineApplication.class, args);
    }

    /**
     * Read CISI dataset documents.
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
        try (BufferedReader reader = Files.newBufferedReader(file)) {

            String line = reader.readLine();
            if (line == null) return null;
            do {
                String docID;
                String docTitle = "";
                String docAuthor = "";
                String docAbstract = "";
                Map<String, String> doc = new HashMap<>(); // docs are dictionaries with the following fields: (ID, title, author, abstract)

                // extract query ID
                docID = line.split(" ")[1];
                doc.put("id", docID);

                boolean extract = true;
                String field = "";
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(".I")) { // when new query ID is found, go to next doc
                        break;
                    }

                    if (line.startsWith(".T")) { // allow to extract doc title field
                        extract = true;
                        field = "title";
                        continue;
                    } else if (line.startsWith(".A")) { // allow to extract doc author field
                        extract = true;
                        field = "author";
                        continue;
                    } else if (line.startsWith(".W")) { // allow to extract doc abstract field
                        extract = true;
                        field = "abstract";
                        continue;
                    } else if (line.startsWith(".B") || line.startsWith(".C") || line.startsWith(".K")) { // don't allow to extract these doc fields
                        extract = false;
                        continue;
                    }

                    if (extract) {
                        if (field.equals("title")) { // extract doc title
                            docTitle = docTitle + " " + line;
                            doc.put("title_norm", docTitle);
                            doc.put("title", docTitle);
                        } else if (field.equals("author")) { // extract doc author
                            docAuthor = docAuthor + " " + line;
                            doc.put("author", docAuthor);
                        } else if (field.equals("abstract")) { // extract doc abstract
                            docAbstract = docAbstract + " " + line;
                            doc.put("abstract_norm", docAbstract);
                            doc.put("abstract", docAbstract);
                        }
                    }
                }

                docs.add(doc);
            } while (line != null);

        } catch (IOException e) {
            System.err.println("[ERROR] readCISIDocuments - Problem occurred while reading the documents.");
            e.printStackTrace();
            return null;
        }

        return docs;
    }

    /**
     * Read CISI dataset queries.
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
        try (BufferedReader reader = Files.newBufferedReader(file)) {

            String line = reader.readLine();
            if (line == null) return null;
            do {
                String queryID;
                String queryText = "";
                Map<String, String> query = new HashMap<>(); // queries are dictionaries with the following fields: (ID, text)

                // extract query ID
                queryID = line.split(" ")[1];
                query.put("id", queryID);

                boolean extract = true;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(".I")) { // when new query ID is found, go to next query
                        break;
                    }

                    if (line.startsWith(".T") || line.startsWith(".W")) { // allow to extract doc title, text fields
                        extract = true;
                        continue;
                    } else if (line.startsWith(".B") || line.startsWith(".A")) { // don't allow to extract these query fields
                        extract = false;
                        continue;
                    }

                    if (extract) { // extract query text
                        queryText = queryText + " " + line;
                        query.put("text", queryText);
                    }
                }

                queries.add(query);
            } while (line != null);

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

        String qrels = "qrels.txt";
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
                tokens = line.trim().split("\\s+|\t+");
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
                    record = queryID + " 0 " + pair.getKey().getField("id").stringValue() + " 0 " + pair.getValue() + " STANDARD";
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
