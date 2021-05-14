package com.bakery.controllers;

import com.bakery.data.OrderElementRepository;
import com.bakery.data.OrderRepository;
import com.bakery.models.Order;
import com.bakery.models.OrderElement;
import com.bakery.models.Product;
import com.bakery.models.User;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes({"selectedTypeofProduction", "currentOrder", "user"})
@RequestMapping("/order")
public class OrderController {

    private final OrderRepository orderRepo;
    private final OrderElementRepository orderElemRepo;

    @Autowired
    public OrderController(OrderRepository orderRepo, OrderElementRepository orderElemRepo) {
        this.orderRepo = orderRepo;
        this.orderElemRepo = orderElemRepo;
    }

    @PostMapping("{id}")
    public String addInCart(@ModelAttribute("selectedTypeofProduction") Integer selectedTypeofProduction,
            @PathVariable("id") Product product,
            @ModelAttribute("quanity") int quanity,
            @ModelAttribute("currentOrder") Order order,
            Model model) {

        OrderElement element = new OrderElement(product, quanity);
        User user = (User) model.getAttribute("user");

        if (order.getOrderDate() == null) {
            order.setOrderDate(new Date());
        }
        if (order.getUser() == null || user != null) {
            order.setUser(user);
            String Address = user.getStreet() + ", д. " + user.getHouse() + ", кв. " + user.getApartment();
            order.setDestination(Address);
        }

        order.addElement(element);

        model.addAttribute("currentOrder", order);

        if (selectedTypeofProduction < 0 || selectedTypeofProduction == null) {
            return "redirect:/catalog";
        } else {
            return "redirect:/catalog/" + String.valueOf(selectedTypeofProduction);
        }
    }

    @GetMapping("/confirmOrder")
    public String confirmOrder(@ModelAttribute("currentOrder") Order order, Model model) {
        return "/order/confirmOrder";
    }

    @GetMapping("/deleteItem/{id}")
    public String deleteFromCart(@PathVariable("id") Product product,
            @ModelAttribute("currentOrder") Order order,
            Model model) {
        order.deleteElement(product);
        model.addAttribute("currentOrder", order);
        if (order.getElement().isEmpty()) {
            return "redirect:/catalog";
        } else {
            return "/order/OrdersList";
        }
    }

    @PostMapping("/OrdersList")
    public String ordersList(@ModelAttribute("currentOrder") Order order,
            HttpServletRequest request,
            Model model) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        List<OrderElement> orderElements = order.getElement();

        for (String key : parameterMap.keySet()) {
            if (!key.equals("_csrf") && !key.equals("destination") && !key.equals("wishes") && !key.equals("fullPrice")) {
                OrderElement element = (OrderElement) orderElements.stream().filter(e -> e.getProduct().getId() == Long.valueOf(key)).findFirst().get();
                element.setQuantity(Integer.valueOf(parameterMap.get(key)[0]));
                orderElemRepo.save(element);
            }
        }
        order.setFullPrice(parameterMap.get("fullPrice")[0]);
        order.setDestination(parameterMap.get("destination")[0]);
        order.setWishes(parameterMap.get("wishes")[0]);
        

        orderRepo.save(order);
        model.addAttribute("currentOrder", new Order());

        return "/order/OrdersList";
    }

}
