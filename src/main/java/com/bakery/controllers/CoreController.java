package com.bakery.controllers;

import com.bakery.models.User;
import com.bakery.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class CoreController {
    
    @Autowired
    UserService userService;

    @RequestMapping("/")
    public String root() {
        return "index";
    }

    @RequestMapping("/contacts")
    public String contacts(){
        return "index";
    }

    @RequestMapping(path = "/profile", method = RequestMethod.GET)
    public String profile(Model model, @AuthenticationPrincipal User user) {
        model.addAttribute("user", user);
        return "profile";
    }

    @RequestMapping(path = "/profile", method = RequestMethod.POST)
    public String saveProfile(Model model, 
                                @AuthenticationPrincipal User user, 
                                @ModelAttribute("user") @Valid User changedUser, 
                                BindingResult bindingResult) {

        if (bindingResult.hasFieldErrors("username")||
                bindingResult.hasFieldErrors("street")||
                bindingResult.hasFieldErrors("house")||
                bindingResult.hasFieldErrors("apartment")||
                bindingResult.hasFieldErrors("phone")) {
            
            model.addAttribute("user", changedUser);
            return "profile";
        }              
        userService.uppdateUser(user, changedUser);

        return "redirect:/catalog";
    }
}
