
package com.bakery.controllers;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AllErrorsController implements ErrorController{
    
    @RequestMapping("/error")
    public String errorHandler(){
        return "error";
    }
    

    @Override
    public String getErrorPath() {
        return null;
    }
    
    
    
}
