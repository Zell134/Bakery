package com.bakery.data;

import com.bakery.models.OrderElement;
import org.springframework.data.repository.CrudRepository;

public interface OrderElementRepository extends CrudRepository<OrderElement, Long>{
    
}
