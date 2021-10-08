package com.lamproslntz.searchengineextended;

import com.lamproslntz.searchengineextended.dto.UserQuery;
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
        model.addAttribute("userQuery", new UserQuery());
        return "index";
    }

}
