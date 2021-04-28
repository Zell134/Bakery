package com.bakery.models;

import java.util.Collection;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
@Table(name = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @NotBlank(message = "ФИО обязательно для заполнения!")
    private String username;
    @NotBlank(message = "Пароль обязателен для заполнения!")
    private String password; 
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
    
    private boolean active;
    
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }

    public User(String username, String password, String email, String street, String house, int apartment, String phone, boolean active, Set<Role> roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.street = street;
        this.house = house;
        this.apartment = apartment;
        this.phone = phone;
        this.active = active;
        this.roles = roles;
    }   
}
