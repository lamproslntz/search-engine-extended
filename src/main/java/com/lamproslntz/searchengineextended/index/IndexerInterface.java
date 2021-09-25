package com.lamproslntz.searchengineextended.index;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Lampros Lountzis
 */
public interface IndexerInterface {

    void create() throws IOException;

    void close() throws IOException;

    void index(List<Map<String, String>> docs) throws IOException;

}
