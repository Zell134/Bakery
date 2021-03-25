package com.bakery.security;

import com.bakery.models.User;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.security.crypto.password.PasswordEncoder;

public class RegistrationForm {

    @NotBlank(message = "ФИО обязательно для заполнения!")
    private String username;
    @NotBlank(message = "Пароль обязателен для заполнения!")
    private String password;
    @NotBlank(message = "Пароли должны совпадать!")
    private String confirm;  
    @NotBlank(message = "Email обязателен для заполнения!")
    @Email(message = "Не корректный формат электронной почты!")
    private String email;
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
        return new User(username,
                passwordEncoder.encode(password),
                email,
                street,
                house,
                apartment,
                phone);
    }

    public RegistrationForm() {
    }
    
    public RegistrationForm(String username, String password, String confirm, String email, String street, String house, int apartment, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
        this.phone = phone;
        this.confirm = confirm;            
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    public int getApartment() {
        return apartment;
    }

    public void setApartment(int apartment) {
        this.apartment = apartment;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirm() {
        return confirm;
    }

    public void setConfirm(String confirm) {
        this.confirm = confirm;
    }
}
