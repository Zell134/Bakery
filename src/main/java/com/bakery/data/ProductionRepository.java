package com.bakery.data;

import com.bakery.models.Product;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface ProductionRepository extends CrudRepository<Product, Long>{
    
    Product findByName(String name);
    List<Product> findByActive(boolean active);
    
    List<Product> findByPriceBetween(String min, String max); 
    List<Product> findByType(long type);
    Product findById(long id);
}
