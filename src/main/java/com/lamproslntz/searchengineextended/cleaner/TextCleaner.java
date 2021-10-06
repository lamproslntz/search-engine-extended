package com.lamproslntz.searchengineextended.cleaner;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.PatternSyntaxException;

/**
 * @author Lampros Lountzis
 */
public class TextCleaner implements TextCleanerInterface {

    private final boolean TO_LOWERCASE;
    private final boolean REMOVE_SYMBOLS;
    private final String SYMBOLS;

    public TextCleaner(boolean toLowercase, boolean removeSymbols) {
        this.TO_LOWERCASE = toLowercase;
        this.REMOVE_SYMBOLS = removeSymbols;
        this.SYMBOLS = "[^a-zA-Z0-9]";
    }

    public TextCleaner(boolean toLowercase, boolean removeSymbols, String symbols) {
        this.TO_LOWERCASE = toLowercase;
        this.REMOVE_SYMBOLS = removeSymbols;
        this.SYMBOLS = symbols;
    }

    public void clean(List<Map<String, String>> objects, String[] fields) {
        for (Map<String, String> obj : objects) {
            for (String field : fields) { // for each obj field to be cleaned
                if (obj.containsKey(field)) { // if the field exists, clean it
                    if (TO_LOWERCASE) {
                        obj.put(field, obj.get(field).toLowerCase(Locale.ROOT));
                    }

                    if (REMOVE_SYMBOLS) {
                        try {
                            obj.put(field, obj.get(field).replaceAll(SYMBOLS, " "));
                        } catch (PatternSyntaxException e) {
                            System.out.println("[ERROR] cleaner.TextCleaner.clean - Invalid regular expression pattern.");
                        }
                    }

                    // remove redundant spaces
                    obj.put(field, obj.get(field).replaceAll("\\s+", " "));
                    obj.put(field, obj.get(field).trim());
                }
            }
        }
    }

    public void fix(List<Map<String, String>> objects, String[] fields) {
        for (Map<String, String> obj : objects) {
            for (String field : fields) { // for each obj field to be fixed
                if (obj.containsKey(field)) { // if the field exists, fix it
                    // remove redundant spaces
                    obj.put(field, obj.get(field).replaceAll("\\s+", " "));
                    obj.put(field, obj.get(field).trim());
                }
            }
        }
    }

}
