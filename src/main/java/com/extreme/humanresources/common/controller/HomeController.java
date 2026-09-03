package com.extreme.humanresources.common.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/dashboard"})
    public String home() {
        return "redirect:/users";
    }
}
