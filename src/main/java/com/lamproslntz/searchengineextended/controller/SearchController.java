package com.lamproslntz.searchengineextended.controller;

import com.lamproslntz.searchengineextended.dto.DocumentDTO;
import com.lamproslntz.searchengineextended.dto.QueryDTO;
import com.lamproslntz.searchengineextended.index.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

/**
 * Controller for handling query submission and look-up.
 *
 * @author Lampros Lountzis
 */
@RestController
public class SearchController {

    private final Searcher searcher;
    private Logger logger = LoggerFactory.getLogger(SearchController.class);

    /**
     * Loads Word2Vec model based on fastText word embeddings (wiki-news-300d-1M.vec),
     * and initializes a Searcher object for searching a Lucene index.
     */
    public SearchController() {
        Word2Vec model = WordVectorSerializer.readWord2VecModel("src/main/resources/fasttext-en/wiki-news-300d-1M.vec");
        this.searcher = new Searcher("src/main/resources/index", model, 0.98);
    }

    /**
     * Searches index for relevant documents with respect to the user query. The Searcher returns
     * the top 20 relevant documents and updates the results section (in index.html).
     *
     * @param queryDTO user query.
     *
     * @return updated page view (index.html) with the relevant documents (data) from the Model.
     */
    @PostMapping("/search")
    public ModelAndView search(@ModelAttribute("userQuery") QueryDTO queryDTO) {
        try {
            logger.info("Opening Lucene index...");
            searcher.open();
        } catch (IOException e) {
            logger.error("An exception was thrown: Could not open Lucene index...", e);
        }

       List<DocumentDTO> results = null;
        try {
            logger.info("Searching Lucene index for documents relevant to the query: \"" + queryDTO.getQuery() + "\"...");
            results = searcher.search(queryDTO, 20);
        } catch (IOException e) {
            logger.error("An exception was thrown: Could not search Lucene index...", e);
        } catch (ParseException e) {
            logger.error("An exception was thrown: Could not parse query...", e);
        }

        try {
            logger.info("Closing Lucene index...");
            searcher.close();
        } catch (IOException e) {
            logger.error("An exception was thrown: Could not close Lucene index...", e);
            e.printStackTrace();
        }

        ModelAndView page = new ModelAndView("index");
        page.addObject("results", results);

        logger.info("Refreshing index.html page with the search results...");

        return page;
    }

}
