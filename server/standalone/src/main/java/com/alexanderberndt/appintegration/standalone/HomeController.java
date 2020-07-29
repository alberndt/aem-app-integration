package com.alexanderberndt.appintegration.standalone;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

@Controller
class HomeController {

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @ModelAttribute("name")
    public String getName() {
        return "Alex B.";
    }
}