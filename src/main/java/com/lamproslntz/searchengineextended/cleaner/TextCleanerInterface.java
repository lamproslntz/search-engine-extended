package com.lamproslntz.searchengineextended.cleaner;

import java.util.List;
import java.util.Map;

/**
 * A TextCleaner cleans a set of documents. Each document consists of a set of fields (e.g. ID, title, author, etc.)
 * each of which can be changed (cleaned) by the TextCleaner. In order to define the cleaning behaviour, subclasses
 * must define the clean({@link List}<{@link Map}<{@link String}, {@link String}>>, {@link String}) method.
 *
 * @author Lampros Lountzis
 */
public interface TextCleanerInterface {

    /**
     * Specifies the operation of cleaning documents.
     *
     * @param text documents and their fields, as a list of dictionaries.
     * @param fields document fields to be cleaned.
     */
    void clean(List<Map<String, String>> text, String[] fields);

}