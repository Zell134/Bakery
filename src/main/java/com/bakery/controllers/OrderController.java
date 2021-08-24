package com.bakery.controllers;

import com.bakery.models.Order;
import com.bakery.models.Product;
import com.bakery.models.User;
import com.bakery.services.OrderService;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"selectedTypeofProduction", "currentOrder"})
@RequestMapping("/order")
public class OrderController {

    private final OrderService service;

    @Autowired
    public OrderController(OrderService service) {
        this.service = service;
    }

    @GetMapping("/info/{id}")
    public String orderInfo(@PathVariable("id") Order order, Model model) {
        model.addAttribute("order", order);
        return "order/orderInfo";
    }

    @PostMapping("{id}")
    public String addInCart(@ModelAttribute("selectedTypeofProduction") Integer selectedTypeofProduction,
            @PathVariable("id") Product product,
            @ModelAttribute("quanity") int quanity,
            @AuthenticationPrincipal User user,
            @ModelAttribute("currentOrder") Order order,
            Model model) {

        model.addAttribute("currentOrder", service.addElement(order, product, quanity, user));

        if (selectedTypeofProduction < 0 || selectedTypeofProduction == null) {
            return "redirect:/catalog";
        } else {
            return "redirect:/catalog/" + String.valueOf(selectedTypeofProduction);
        }
    }

    @GetMapping("/confirmOrder")
    public String confirmOrder(@ModelAttribute("currentOrder") Order order, Model model) {
        return "order/confirmOrder";
    }

    @GetMapping("/deleteItem/{id}")
    public String deleteFromCart(@PathVariable("id") Product product,
            @ModelAttribute("currentOrder") Order order,
            Model model) {

        service.deleteElement(order, product);
        model.addAttribute("currentOrder", order);
        if (order.getElement().isEmpty()) {
            return "redirect:/catalog";
        } else {
            return "order/confirmOrder";
        }
    }

    @GetMapping("/ordersList")
    public String ordersList(
            @AuthenticationPrincipal User user,
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        Page<Order> page = service.findUserOrders(user, pageable);
        model.addAttribute("orders", page);
        return "order/list";
    }

    @PostMapping("/ordersList")
    public String postOrdersList(@ModelAttribute("currentOrder") Order order,
            HttpServletRequest request,
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {

        service.saveOrder(order, request);
        model.addAttribute("orders", service.findUserOrders(order.getUser(), pageable));
        model.addAttribute("currentOrder", new Order());
        return "order/list";
    }

    @PostMapping("/sortByDate")
    public String sortByDate(
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam(required = false) String startDate,
            @DateTimeFormat(pattern = "yyyy-MM-dd")@RequestParam(required = false) String endDate,
            @AuthenticationPrincipal User user,
            Model model,
            @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC) Pageable pageable
    ) {
        model.addAttribute("error", null);
        Page<Order> page = service.findOrdersBetweenOrderDate(user, startDate, endDate, pageable);
        if (startDate == null || endDate == null || page == null) {
            page = service.findUserOrders(user, pageable);
            model.addAttribute("error", "Необходимо установить дату!");
            model.addAttribute("orders", page);
            return "order/list";
        }        
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("orders", page);
        return "order/list";
    }

}
