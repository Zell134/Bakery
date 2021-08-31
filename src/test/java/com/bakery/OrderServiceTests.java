package com.bakery;

import com.bakery.data.OrderElementRepository;
import com.bakery.data.OrderRepository;
import com.bakery.models.Order;
import com.bakery.models.Product;
import com.bakery.models.User;
import com.bakery.services.MailSender;
import com.bakery.services.OrderService;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class OrderServiceTests {

    @Autowired
    private OrderService orderService;

    @MockBean
    private OrderRepository orderRepo;
    @MockBean
    private OrderElementRepository orderElemRepo;
    @MockBean
    private MailSender mailSender;
    
    private Order setOrder(){
        User user = new User(); 
        Order order = new Order();
        Product product = new Product();
        product.setName("productName");
        product.setPrice(new BigDecimal("10"));
        
        return order = orderService.addElement(order, product, 2, user);        
    }
    
    @Test
    public void addElementTest(){
        
        Order order = setOrder();   
        
        assertEquals("productName", order.getElement().get(0).getProduct().getName());
        assertEquals(new BigDecimal("20"), order.getFullPrice());
        assertEquals(2, order.getElement().get(0).getQuantity());
        
        Product product2 = new Product();        
        product2.setName("productName2");
        product2.setPrice(new BigDecimal("20"));
        order = orderService.addElement(order, product2, 1, new User());
        
        assertEquals(2, order.getElement().size());
        assertEquals("productName2", order.getElement().get(1).getProduct().getName());
        assertEquals(new BigDecimal("40"), order.getFullPrice());
        assertEquals(1, order.getElement().get(1).getQuantity());
    }
    
    @Test 
    public void deleteElementTest(){
        Order order = setOrder();
        assertEquals(1, order.getElement().size());
        
        orderService.deleteElement(order, order.getElement().get(0).getProduct());
        assertEquals(0, order.getElement().size());
    }

}
