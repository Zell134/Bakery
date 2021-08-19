package com.bakery.service;

import com.bakery.data.OrderElementRepository;
import com.bakery.data.OrderRepository;
import com.bakery.data.TypeRepository;
import com.bakery.models.Order;
import com.bakery.models.OrderElement;
import com.bakery.models.Product;
import com.bakery.models.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class OrderService {

    private final OrderRepository orderRepo;
    private final OrderElementRepository orderElemRepo;
    private final MailSenderService mailSender;
    private final TypeRepository typeRepo;
    @Value("${spring.mail.username}")
    private String mailToOrders;

    @Autowired
    public OrderService(OrderRepository orderRepo, OrderElementRepository orderElemRepo, MailSenderService mailSender, TypeRepository typeRepo) {
        this.orderRepo = orderRepo;
        this.orderElemRepo = orderElemRepo;
        this.mailSender = mailSender;
        this.typeRepo = typeRepo;
    }

    public Order addElement(Order order,
            Product product,
            int quantity,
            User user) {

        OrderElement element = new OrderElement(product, quantity);

        if (order.getOrderDate() == null) {
            order.setOrderDate(LocalDateTime.now());
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

    public Page<Order> findUserOrders(User user, Pageable pageable) {
        return orderRepo.findByUser(user, pageable);
    }

    public void saveOrder(Order order, HttpServletRequest request) {

        Map<String, String[]> parameterMap = request.getParameterMap();
        List<OrderElement> orderElements = order.getElement();

        parameterMap.keySet().forEach(key -> {
            if (!key.equals("_csrf") && !key.equals("destination") && !key.equals("wishes") && !key.equals("fullPrice")) {
                OrderElement element = (OrderElement) orderElements.stream().filter(e -> e.getProduct().getId() == Long.valueOf(key)).findFirst().get();
                element.setQuantity(Integer.valueOf(parameterMap.get(key)[0]));
                orderElemRepo.save(element);
            }
        });
        order.setFullPrice(parameterMap.get("fullPrice")[0]);
        order.setDestination(parameterMap.get("destination")[0]);
        order.setWishes(parameterMap.get("wishes")[0]);
        order.setOrderDate(LocalDateTime.now());
        order.setId(0);

        sendMail(orderRepo.save(order));
    }

    public void sendMail(Order order) {
        String мessage = "Оформлен заказ № " + order.getId() + ".\n\n";

        int counter = 1;
        for (OrderElement element : order.getElement()) {
            мessage += String.format("%d. %s. %s, Количество - %d. \n",
                    counter,
                    typeRepo.findById(element.getProduct().getType()).getName(),
                    element.getProduct().getName(),
                    element.getQuantity()
            );
            counter++;
        }
        мessage += "Сумма заказа - " + order.getFullPrice() + ".\n\n";
        мessage += "Адрес доставки - " + order.getDestination() + "\n";
        мessage += "Контактное лицо - " + order.getUser().getUsername() + "\n";
        мessage += "Телефон - " + order.getUser().getPhone() + "\n";

        mailSender.send(mailToOrders, "Заказ № " + order.getId(), мessage);
        mailSender.send(order.getUser().getEmail(), "Заказ № " + order.getId(), мessage);
    }

    public Page<Order> findOrdersBetweenOrderDate(User user, String startDate, String endDate, Pageable pageable) {
        LocalDateTime start;
        LocalDateTime end;
        try {
            start = LocalDate.parse(startDate).atStartOfDay();
            end = LocalDate.parse(endDate).atTime(23, 59);
        } catch (DateTimeParseException e) {
            return null;
        }
        return orderRepo.findByUserAndOrderDateBetween(user, start, end, pageable);
    }
}
