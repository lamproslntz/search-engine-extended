package cleaner;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * @author Lampros Lountzis
 */
public class TextCleaner implements TextCleanerInterface {

    private final boolean toLowercase;
    private final boolean removeSymbols;
    private final String symbols;

    public TextCleaner(boolean toLowercase, boolean removeSymbols) {
        this.toLowercase = toLowercase;
        this.removeSymbols = removeSymbols;
        this.symbols = "[^a-zA-Z0-9]";
    }

    public TextCleaner(boolean toLowercase, boolean removeSymbols, String symbols) {
        this.toLowercase = toLowercase;
        this.removeSymbols = removeSymbols;
        this.symbols = symbols;
    }

    public void clean(List<Map<String, String>> objects, String[] fields) {
        for (Map<String, String> obj : objects) {
            for (String field : fields) { // for each obj field to be cleaned
                if (obj.containsKey(field)) { // if the field exists, clean it
                    if (toLowercase) {
                        obj.put(field, obj.get(field).toLowerCase(Locale.ROOT));
                    }

                    if (removeSymbols) {
                        try {
                            obj.put(field, obj.get(field).replaceAll(symbols, " "));
                        } catch (PatternSyntaxException e) {
                            System.out.println("[ERROR] cleaner.TextCleaner.clean - Invalid regular expression pattern.");
                        }
                    }

                    // remove redundant spaces
                    obj.put(field, obj.get(field).replaceAll("\s+", " "));
                    obj.put(field, obj.get(field).trim());
                }
            }
        }
    }

}
