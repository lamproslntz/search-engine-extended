package com.lamproslntz.searchengineextended.controller;

import com.lamproslntz.searchengineextended.dto.QueryDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for handling homepage (index.html).
 *
 * @author Lampros Lountzis
 */
@Controller
public class IndexController {

    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    /**
     * Loads homepage (index.html).
     *
     * @param model contains requested data and provides it to view page.
     *
     * @return homepage (index.html)
     */
    @GetMapping("/")
    public String loadIndex(Model model) {
        model.addAttribute("userQuery", new QueryDTO());

        logger.info("Loading index.html page...");

        return "index";
    }

}
