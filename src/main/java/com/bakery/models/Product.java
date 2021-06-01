package com.bakery.models;

import java.io.Serializable;
import java.math.BigDecimal;
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

@Entity
@Table(name = "Production")
@Data
@AllArgsConstructor
public class Product implements Serializable {
    
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @NotBlank(message = "Название обязательно для заполнения!")
    private String name;

    @Column(length = 2048)
    @NotBlank(message = "Описание обязательно для заполнения!")
    private String description;

    @NotNull(message = "Цена обязательна для заполнения!")
    private BigDecimal price;

    @NotNull(message = "В наличии?")
    private boolean active;

    @NotNull(message = "Выберите тип продукции!")
    private long type;

    private String imageUrl;

    public String getPrice() {
        return String.valueOf(this.price);
    }

    public Product() {
        this.name = "";
        this.description = "";
        this.price = new BigDecimal("0");
        this.active = true;
        this.type = 0;
        this.imageUrl = "";
    }

}
