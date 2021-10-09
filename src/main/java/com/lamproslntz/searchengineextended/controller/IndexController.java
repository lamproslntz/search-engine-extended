package com.lamproslntz.searchengineextended.controller;

import com.lamproslntz.searchengineextended.dto.QueryDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Lampros Lountzis
 */
@Controller
public class IndexController {

    @GetMapping("/")
    public String loadIndex(Model model) {
        model.addAttribute("userQuery", new QueryDTO());

        return "index";
    }

}
