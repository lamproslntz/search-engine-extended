package com.lamproslntz.searchengineextended.index;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * An Indexer indexes all files in a directory and leaves behind a searchable Lucene index.
 * In order to define the indexing behaviour, subclasses must define create(), close() and
 * index({@link List}<{@link Map}<{@link String}, {@link String}>>) methods.
 *
 * @author Lampros Lountzis
 */
public interface IndexerInterface {

    /**
     * Specifies the policy used to create a Lucene index.
     * To create an index a directory policy, an analyzer and a similarity function should be specified.
     *
     * @throws IOException if the directory cannot host the index files.
     */
    void create() throws IOException;

    /**
     * Specifies the policy for freeing persistent resources used by this Indexer.
     *
     * @throws IOException if the Indexer is closed.
     */
    void close() throws IOException;

    /**
     * Specifies the operation of indexing documents and their field.
     *
     * @param docs documents to be indexed, as a list of dictionaries.
     *
     * @throws IOException if the Indexer is closed.
     */
    void index(List<Map<String, String>> docs) throws IOException;

}
