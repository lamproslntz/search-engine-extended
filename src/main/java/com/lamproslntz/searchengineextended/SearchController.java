package com.lamproslntz.searchengineextended;

import com.lamproslntz.searchengineextended.dto.RetrievedItem;
import com.lamproslntz.searchengineextended.dto.UserQuery;
import com.lamproslntz.searchengineextended.index.Searcher;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.List;

/**
 * @author Lampros Lountzis
 */
@RestController
public class SearchController {

//    private final String MODEL_PATH = "src/main/resources/fasttext-en/wiki-news-300d-1M.vec";
//    private final Word2Vec MODEL = WordVectorSerializer.readWord2VecModel(MODEL_PATH);
//
//    private final double MIN_ACCURACY = 0.98;

    @PostMapping("/search")
    public ModelAndView search(@ModelAttribute("userQuery") UserQuery userQuery) {
        Searcher searcher = new Searcher("src/main/resources/index");

        try {
            searcher.open();
        } catch (IOException e) {
            // TODO: logging
            e.printStackTrace();
        }

       List<RetrievedItem> results = null;
        try {
            results = searcher.search(userQuery, 20);
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
