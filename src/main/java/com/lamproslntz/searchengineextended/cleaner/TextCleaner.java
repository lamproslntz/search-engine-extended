package com.lamproslntz.searchengineextended.cleaner;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * Represents a TextCleaner module, for cleaning a set of documents. Each document consists of a set of fields, which
 * will be cleaned by lowercasing the text, removing symbols from it and redundant spaces.
 *
 * @author Lampros Lountzis
 */
public class TextCleaner implements TextCleanerInterface {

    private final boolean TO_LOWERCASE;
    private final boolean REMOVE_SYMBOLS;
    private final String SYMBOLS;

    /**
     * Initializes TextCleaner.
     *
     * @param toLowercase boolean flag for whether to lowercase text.
     * @param removeSymbols boolean flag for whether to remove symbols.
     */
    public TextCleaner(boolean toLowercase, boolean removeSymbols) {
        this.TO_LOWERCASE = toLowercase;
        this.REMOVE_SYMBOLS = removeSymbols;
        this.SYMBOLS = "[^a-zA-Z0-9]";
    }

    /**
     * Initializes TextCleaner.
     *
     * @param toLowercase boolean flag for whether to lowercase text.
     * @param symbols string of symbols to be removed, as a regex.
     */
    public TextCleaner(boolean toLowercase, String symbols) {
        this.TO_LOWERCASE = toLowercase;
        this.REMOVE_SYMBOLS = true;
        this.SYMBOLS = symbols;
    }

    /**
     * Cleans document fields.
     * The following operations take place:
     *  * lowercasing text,
     *  * removal of symbols,
     *  * removal of redundant spaces.
     *
     * @param text documents and their fields, as a list of dictionaries.
     * @param fields document fields to be cleaned.
     */
    public void clean(List<Map<String, String>> text, String[] fields) {
        for (Map<String, String> txt : text) {
            for (String field : fields) { // for each obj field to be cleaned
                if (txt.containsKey(field)) { // if the field exists, clean it
                    if (TO_LOWERCASE) {
                        txt.put(field, txt.get(field).toLowerCase(Locale.ROOT));
                    }

                    if (REMOVE_SYMBOLS) {
                        try {
                            txt.put(field, txt.get(field).replaceAll(SYMBOLS, " "));
                        } catch (PatternSyntaxException e) {
                            System.out.println("[ERROR] cleaner.TextCleaner.clean - Invalid regular expression pattern.");
                        }
                    }

                    // remove redundant spaces
                    txt.put(field, txt.get(field).replaceAll("\\s+", " "));
                    txt.put(field, txt.get(field).trim());
                }
            }
        }
    }

    /**
     * Fixes document field structure.
     * The following operations take place:
     *  * remove redundant spaces.
     *
     * @param text documents and their fields, as a list of dictionaries.
     * @param fields document fields to be fixed.
     */
    public void fix(List<Map<String, String>> text, String[] fields) {
        for (Map<String, String> txt : text) {
            for (String field : fields) { // for each obj field to be fixed
                if (txt.containsKey(field)) { // if the field exists, fix it
                    // remove redundant spaces
                    txt.put(field, txt.get(field).replaceAll("\\s+", " "));
                    txt.put(field, txt.get(field).trim());
                }
            }
        }
    }

}
