package com.lamproslntz.searchengineextended.controller;

import com.lamproslntz.searchengineextended.dto.DocumentDTO;
import com.lamproslntz.searchengineextended.dto.QueryDTO;
import com.lamproslntz.searchengineextended.index.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.Word2Vec;
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
            searcher.open();
        } catch (IOException e) {
            // TODO: logging
            e.printStackTrace();
        }

       List<DocumentDTO> results = null;
        try {
            results = searcher.search(queryDTO, 20);
        } catch (IOException e) {
            // TODO: logging
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO: logging
            e.printStackTrace();
        }

        try {
            searcher.close();
        } catch (IOException e) {
            // TODO: logging
            e.printStackTrace();
        }

        ModelAndView page = new ModelAndView("index");
        page.addObject("results", results);

        return page;
    }

}
