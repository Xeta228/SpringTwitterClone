package com.baron.webapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String testView(){
        System.out.println("Just testing things");
        return "noAutthority";
    }

    @GetMapping("/html") //not working because not compatible with other view-resolver
    public String testHtmlResolving(){
        System.out.println("reached the testHtmlResolving method");
        return "html_test_page";
    }
}
