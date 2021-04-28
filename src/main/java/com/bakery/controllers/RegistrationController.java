package com.bakery.controllers;

import com.bakery.data.UserRepository;
import com.bakery.security.RegistrationForm;
import javax.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        if(userRepository.findByEmail(form.getEmail())!=null){
            bindingResult.addError(new FieldError("email", "email", "Пользователь с такой электронной почтой уже существует!"));
            return "registration";
        }
        userRepository.save(form.toUser(passwordEncoder));
        return "redirect:/login";
    }

}
