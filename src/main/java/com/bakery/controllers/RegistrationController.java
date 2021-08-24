package com.bakery.controllers;

import com.bakery.models.User;
import com.bakery.models.RegistrationForm;
import com.bakery.services.UserService;
import java.net.UnknownHostException;
import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    public String RegistrationProcess(
            @ModelAttribute("form") @Valid RegistrationForm form, 
            BindingResult bindingResult, 
            Model model,
            RedirectAttributes redirectAttributes,
            @RequestHeader String origin
    ) throws UnknownHostException {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        if (!(form.getPassword().equals(form.getConfirm()))) {
            bindingResult.addError(new FieldError("confirm", "confirm", "Пароли должны совпадать!"));
            return "registration";
        }
        User user = userService.addUser(form, origin);
        if (user == null) {
            bindingResult.addError(new FieldError("email", "email", "Пользователь с такой электронной почтой уже существует!"));
            return "registration";
        }
        
        redirectAttributes.addFlashAttribute("message", "Для завершения регистрации необходимо пройти по ссылеке на электронной почте!");

        return "redirect:/login";
    }

    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code) {
        boolean isActivated = userService.activateUser(code);
        if (isActivated) {
            System.out.println("User is activated!");
        }
        return "login";
    }

    @PostMapping("/remindPassword")
    @ResponseBody
    public boolean remindPassword(@RequestParam(name = "email", required = false) String email, Model model) {

        if (!userService.remindPassword(email)) {
            return false;
        }
        return true;
    }

    @GetMapping("/changePassword/{id}")
    public String changePassword(@PathVariable("id") User user, Model model) {
        model.addAttribute("user", user);
        return "users/changePassword";
    }

    @PostMapping("/changePassword/{id}")
    public String changePswrd(
            @RequestParam("password") String password,
            @RequestParam("confirm") String confirm,
            @PathVariable("id") User user,
            @AuthenticationPrincipal User currentUser,
            Model model
    ) {
        model.addAttribute("error", null);
        if (StringUtils.isEmpty(confirm) || StringUtils.isEmpty(password)) {
            model.addAttribute("error", "Пароли не должны быть пустыми!");
            model.addAttribute("user", user);
            return "users/changePassword";
        }
        if (!password.equals(confirm)) {
            model.addAttribute("error", "Пароли должны совпадать!");
            model.addAttribute("user", user);
            return "users/changePassword";
        }
        if (user.getId() == currentUser.getId()) {
            userService.changePassword(currentUser, password);
        }
        return "redirect:/profile";
    }

}
