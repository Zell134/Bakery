package com.bakery.models;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RegistrationForm extends User {

    private String confirm;

        public User toUser() {
        Set<Role> rolesSet = new HashSet<>();
        rolesSet.add(Role.USER);
        setRoles(rolesSet);
        return new User(getUsername(),
                getPassword(),
                getEmail(),
                getStreet(),
                getHouse(),
                getApartment(),
                getPhone(), getRoles());
    }
}
