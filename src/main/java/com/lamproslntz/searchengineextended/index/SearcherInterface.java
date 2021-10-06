package com.lamproslntz.searchengineextended.index;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * @author Lampros Lountzis
 */
public interface SearcherInterface {

    List<Pair<Document, Float>> search(String userQuery, int k) throws IOException, ParseException;

    void open() throws IOException;

    void close() throws IOException;

}
