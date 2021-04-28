package com.bakery.controllers;

import com.bakery.data.UserRepository;
import com.bakery.models.Role;
import com.bakery.models.User;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/user")
public class UserController {

    UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "/users/userList";
    }

    @GetMapping("{user}")
    public String userEditForm(@PathVariable User user, Model model) {
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "/users/userEdit";
    }

    @PostMapping
    public String saveUser(
            @RequestParam("id") User user,
            @RequestParam("username") String username,
            @RequestParam("street") String street,
            @RequestParam("house") String house,
            @RequestParam("apartment") int apartment,
            @RequestParam("phone") String phone,
            @RequestParam("isActive") boolean isActive,
            @RequestParam("roles")Set<Role> roles) {

        user.setUsername(username);
        user.setStreet(street);
        user.setHouse(house);
        user.setApartment(apartment);
        user.setPhone(phone);
        user.setActive(isActive);
        user.setRoles(roles);
        userRepository.save(user);
        return "redirect:/user";
    }
}
