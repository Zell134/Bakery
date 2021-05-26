package com.bakery.data;

import com.bakery.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long>{
    
    User findByUsername(String username);
    User findByEmailIgnoreCase(String email);

    public User findByActivationCode(String code);
}
