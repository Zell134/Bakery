package com.bakery.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CoreController {
    
    @RequestMapping("/")
    public String root(){
        return "index";
    }    
}
