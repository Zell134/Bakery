package com.bakery.models;

import java.util.HashSet;
import java.util.Set;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@NoArgsConstructor
public class RegistrationForm extends User {

    private String confirm;

        public User toUser(PasswordEncoder passwordEncoder) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        setRoles(roles);
        return new User(getUsername(),
                passwordEncoder.encode(getPassword()),
                getEmail(),
                getStreet(),
                getHouse(),
                getApartment(),
                getPhone(), getRoles());
    }
}
