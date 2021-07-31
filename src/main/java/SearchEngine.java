import file.CISIFileHandler;
import index.Indexer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Lampros Lountzis
 */
public class SearchEngine {
    public static void main(String[] args) {
        // read documents
        System.out.println("[INFO] SearchEngine.main - Reading documents...");
        List<Map<String, String>> docs = CISIFileHandler.readCISIDocuments("C:\\Users\\lampr\\Downloads\\cisi\\CISI.ALL");
        if (docs == null) {
            System.exit(1);
        }

        // read queries
        System.out.println("[INFO] SearchEngine.main - Reading queries...");
        List<Map<String, String>> queries = CISIFileHandler.readCISIQueries("C:\\Users\\lampr\\Downloads\\cisi\\CISI.QRY");
        if (queries == null) {
            System.exit(1);
        }

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
    }
}
