package com.bakery.models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Production")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    
    @NotBlank(message = "Название обязательно для заполнения!")
    private String name;
    
    @Column(length = 2048)
    @NotBlank(message = "Описание обязательно для заполнения!")
    private String description;
    
    @NotNull(message = "Цена обязательна для заполнения!")
    @Min(value = 1, message = "Цена обязательна для заполнения!")
    private float price;
    
    @NotNull(message = "В наличии?")
    private boolean active;
    
    @NotNull(message = "Выберите тип продукции!")
    private long type;
    
    private String imageUrl;
}
