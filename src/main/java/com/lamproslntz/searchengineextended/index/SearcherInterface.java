package com.lamproslntz.searchengineextended.index;

import com.lamproslntz.searchengineextended.dto.QueryDTO;
import com.lamproslntz.searchengineextended.dto.DocumentDTO;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * A Searcher is used to search for documents in the Lucene index, with respect to a query.
 * In order to define the searching behaviour, subclasses must define open(), close()
 * and search({@link List}<{@link DocumentDTO}>) methods.
 *
 * @author Lampros Lountzis
 */
public interface SearcherInterface {

    /**
     * Specifies the policy used to search a Lucene index.
     * To search an index, the searchable document fields, an analyzer, a similarity function and a query parser
     * should be specified.
     *
     * @param userQuery the user's query.
     * @param k number of top documents to be retrieved.
     *
     * @return list of top k retrieved documents, with respect to the user's query.
     *
     * @throws IOException if the Lucene index cannot be searched.
     * @throws ParseException if the user's query cannot be parsed.
     */
    List<DocumentDTO> search(QueryDTO userQuery, int k) throws IOException, ParseException;

    /**
     * Specifies the policy for opening the Lucene index to be used by this Searcher.
     *
     * @throws IOException if the Lucene index cannot be opened.
     */
    void open() throws IOException;

    /**
     * Specifies the policy for freeing persistent resources used by this Searcher.
     *
     * @throws IOException if the Searcher is closed.
     */
    void close() throws IOException;

}
