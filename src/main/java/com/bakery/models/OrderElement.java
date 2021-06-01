package com.bakery.models;

import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class OrderElement implements Serializable {
    
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    private BigDecimal price;

    public OrderElement(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        this.price = new BigDecimal(product.getPrice()).multiply(new BigDecimal(String.valueOf(quantity)));
    }

    public void setProduct(Product product) {
        this.product = product;
        this.price = new BigDecimal(product.getPrice()).multiply(new BigDecimal(String.valueOf(this.quantity)));
    }

    public void setQuantity(int quanity) {
        this.quantity = quanity;
        this.price = new BigDecimal(product.getPrice()).multiply(new BigDecimal(String.valueOf(this.quantity)));
    }

    public void plusQuantity(int quantity) {
        this.quantity += quantity;
        this.price = new BigDecimal(product.getPrice()).multiply(new BigDecimal(String.valueOf(this.quantity)));
    }
}
