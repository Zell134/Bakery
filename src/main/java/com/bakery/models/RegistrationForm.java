package com.bakery.models;

import com.bakery.models.Role;
import com.bakery.models.User;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationForm implements Serializable {
    
    static final long serialVersionUID = 1L;

    @NotBlank(message = "ФИО обязательно для заполнения!")
    private String username;
    @NotBlank(message = "Пароль обязателен для заполнения!")
    private String password;
    @NotBlank(message = "Пароли должны совпадать!")
    private String confirm;
    @NotBlank(message = "Email обязателен для заполнения!")
    @Email(message = "Не корректный формат электронной почты!")
    private String email;
    private String activationCode;
    @NotBlank(message = "Улица обязателена для заполнения!")
    private String street;
    @NotBlank(message = "Дом обязателен для заполнения!")
    private String house;
    @Min(value = 1, message = "Квартира обязателен для заполнения!")
    @NotNull(message = "Квартира обязателен для заполнения!")
    private int apartment;
    @NotBlank(message = "Номер телефона обязателен для заполнения!")
    private String phone;

    public User toUser(PasswordEncoder passwordEncoder) {
        Set<Role> roles = new HashSet<>();
        roles.add(Role.USER);
        return new User(username,
                passwordEncoder.encode(password),
                email,
                street,
                house,
                apartment,
                phone, roles);
    }
}
