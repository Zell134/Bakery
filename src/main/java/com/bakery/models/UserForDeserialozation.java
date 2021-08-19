package com.bakery.models;

import java.io.Serializable;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserForDeserialozation implements Serializable{
    
    static final long serialVersionUID = 1L;

    long id;

    private String username;
    private String password; 
    private String email;
    private String activationCode;
    private String street;
    private String house;
    private int apartment;
    private String phone;
    private boolean active;
    private Set<Role> roles;
    
    public User toUser(){
        User user = new User();
        
        user.setId(id);       
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setActivationCode(activationCode);
        user.setStreet(street);
        user.setHouse(house);
        user.setApartment(apartment);
        user.setPhone(phone);
        user.setActive(active);
        user.setRoles(roles);
        
        return user;
    }
}
