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
 * @author Lampros Lountzis
 */
@RestController
public class SearchController {

    private final Searcher searcher;

    public SearchController() {
        Word2Vec model = WordVectorSerializer.readWord2VecModel("src/main/resources/fasttext-en/wiki-news-300d-1M.vec");
        this.searcher = new Searcher("src/main/resources/index", model, 0.98);
    }

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
