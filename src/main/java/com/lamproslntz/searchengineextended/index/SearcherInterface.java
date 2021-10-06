package com.lamproslntz.searchengineextended.index;

import com.lamproslntz.searchengineextended.dto.RetrievedItem;
import com.lamproslntz.searchengineextended.dto.UserQuery;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.util.List;

/**
 * @author Lampros Lountzis
 */
public interface SearcherInterface {

    List<RetrievedItem> search(UserQuery userQuery, int k) throws IOException, ParseException;

    void open() throws IOException;

    void close() throws IOException;

}
