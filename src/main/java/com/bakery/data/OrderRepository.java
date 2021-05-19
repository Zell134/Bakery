package com.bakery.data;

import com.bakery.models.Order;
import com.bakery.models.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long>{
    public List<Order> findByUser(User user);
    
}
