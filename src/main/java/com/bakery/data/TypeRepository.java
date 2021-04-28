package com.bakery.data;

import com.bakery.models.Type;
import org.springframework.data.repository.CrudRepository;

public interface TypeRepository extends CrudRepository<Type, Long> {
    
    public Type findByName(String name);
    
}
