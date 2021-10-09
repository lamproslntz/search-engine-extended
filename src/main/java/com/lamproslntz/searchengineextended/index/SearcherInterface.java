package com.lamproslntz.searchengineextended.index;

import com.lamproslntz.searchengineextended.dto.QueryDTO;
import com.lamproslntz.searchengineextended.dto.DocumentDTO;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * @author Lampros Lountzis
 */
public interface SearcherInterface {

    List<DocumentDTO> search(QueryDTO userQuery, int k) throws IOException, ParseException;

    void open() throws IOException;

    void close() throws IOException;

}
