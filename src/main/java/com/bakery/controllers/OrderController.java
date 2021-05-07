package com.bakery.controllers;

import com.bakery.models.Order;
import com.bakery.models.Product;
import com.bakery.models.User;
import java.util.Date;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"selectedTypeofProduction", "currentOrder", "user"})
@RequestMapping("/order")
public class OrderController {

    @PostMapping("{id}")
    public String addInCart(@ModelAttribute("selectedTypeofProduction") int selectedTypeofProduction,
            @PathVariable("id") Product product,
            @ModelAttribute("quanity") int quanity,
            @ModelAttribute("currentOrder") Order order,
            @ModelAttribute("user") User user,
            Model model) {

        if (order.getOrderDate() == null) {
            order.setOrderDate(new Date());
        }
        if(order.getUser() == null){
            order.setUser(user);
        }

        System.out.println(order);

        if (selectedTypeofProduction < 0) {
            return "redirect:/catalog";
        } else {
            return "redirect:/catalog/" + String.valueOf(selectedTypeofProduction);
        }

    }

}
