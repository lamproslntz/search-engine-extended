package cleaner;

import java.util.List;
import java.util.Map;

/**
 * @author Lampros Lountzis
 */
public interface TextCleanerInterface {

    void clean(List<Map<String, String>> text, String[] fields);

}