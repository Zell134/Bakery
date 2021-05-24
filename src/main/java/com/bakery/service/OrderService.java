package com.bakery.service;

import com.bakery.data.OrderElementRepository;
import com.bakery.data.OrderRepository;
import com.bakery.models.Order;
import com.bakery.models.OrderElement;
import com.bakery.models.Product;
import com.bakery.models.User;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderElementRepository orderElemRepo;
    private final MailSender mailSender;
    @Value("${spring.mail.username}")
    private String mailToOrders;

    @Autowired
    public OrderService(OrderRepository orderRepo, OrderElementRepository orderElemRepo, MailSender mailSender) {
        this.orderRepo = orderRepo;
        this.orderElemRepo = orderElemRepo;
        this.mailSender = mailSender;
    }

    public Order addElement(Order order,
            Product product,
            int quantity,
            User user) {

        OrderElement element = new OrderElement(product, quantity);

        if (order.getOrderDate() == null) {
            order.setOrderDate(new Date());
        }
        if (order.getUser() == null || user != null) {
            order.setUser(user);
            String Address = user.getStreet() + ", д. " + user.getHouse() + ", кв. " + user.getApartment();
            order.setDestination(Address);
        }

        order.addElement(element);

        return order;
    }

    public void deleteElement(Order order,
            Product product) {
        order.deleteElement(product);
    }

    public List<Order> findUserOrders(User user) {
        return orderRepo.findByUser(user);
    }

    public void saveOrder(Order order, HttpServletRequest request) {

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
        sendMail(order);
    }

    public void sendMail(Order order) {
        String мessage = "Оформлен заказ № " + order.getId() + ".\n\n";

        int counter = 1;
        for (OrderElement element : order.getElement()) {
            мessage += String.format("%d. %s, Количество - %d. \n", counter, element.getProduct().getName(), element.getQuantity());
            counter++;
        }
        мessage += "Сумма заказа - " + order.getFullPrice() + ".\n\n";
        мessage += "Адрес доставки - " + order.getDestination() + "\n";
        мessage += "Контактное лицо - " + order.getUser().getUsername() + "\n";
        мessage += "Телефон - " + order.getUser().getPhone() + "\n";

        mailSender.send(mailToOrders, "Заказ № " + order.getId(), мessage);
        mailSender.send(order.getUser().getEmail(), "Заказ № " + order.getId(), мessage);
    }
}
