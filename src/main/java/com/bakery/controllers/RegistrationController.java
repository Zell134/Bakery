package com.bakery.controllers;

import com.bakery.models.User;
import com.bakery.models.RegistrationForm;
import com.bakery.service.UserService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserService userService;

    @Autowired
    public RegistrationController(UserService service) {
        this.userService = service;
    }    

    @ModelAttribute("form")
    public RegistrationForm form() {
        return new RegistrationForm();
    }

    @GetMapping
    public String registrationForm() {
        return "registration";
    }

    @PostMapping
    public String RegistrationProcess(@ModelAttribute("form") @Valid RegistrationForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!(form.getPassword().equals(form.getConfirm()))) {
            bindingResult.addError(new FieldError("confirm", "confirm", "Пароли должны совпадать!"));
            return "registration";
        }
        User user = userService.addUser(form);
        if(user == null){
            bindingResult.addError(new FieldError("email", "email", "Пользователь с такой электронной почтой уже существует!"));
            return "registration";
        }
        
        return "redirect:/login";
    }
    
    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActivated = userService.activateUser(code);
        if(isActivated)
            System.out.println("User is activated!");
        return "login";
    }

}
