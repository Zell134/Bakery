package com.bakery.data;

import com.bakery.models.Order;
import com.bakery.models.User;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface OrderRepository extends CrudRepository<Order, Long>{


    public Page<Order> findAll(Pageable pageable);
    
    public Page <Order> findByUser(User user, Pageable pageable);
    
    public Page <Order> findByUserAndOrderDateBetween(User user, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    
}
