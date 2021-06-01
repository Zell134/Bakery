package com.bakery.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "orders")
public class Order implements Serializable{
    
    static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private LocalDateTime orderDate;

    @OneToMany
    @JoinColumn(name = "element_id")
    List<OrderElement> element;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    
    private BigDecimal fullPrice;
    
    private String destination;
    
    String wishes;

    public Order() {
        this.fullPrice = new BigDecimal("0");
        this.element = new ArrayList<>();
        this.orderDate = null;
        this.user = null;
        this.destination = "";
        this.wishes = "";
    }

    public void addElement(OrderElement element) {
        Optional<OrderElement> elemIfExist = this.element.stream().filter(elem -> elem.getProduct().equals(element.getProduct())).findFirst();
        if (!elemIfExist.isEmpty()) {
            int index = this.element.indexOf(elemIfExist.get());
            this.element.get(index).plusQuantity(element.getQuantity());
            this.fullPrice = this.fullPrice.add(element.getPrice());

        } else {
            this.element.add(element);
            this.fullPrice = this.fullPrice.add(element.getPrice());
        }
    }

    public void deleteElement(Product product) {
        Optional<OrderElement> elemIfExist = this.element.stream().filter(elem -> elem.getProduct().equals(product)).findFirst();
        if (!elemIfExist.isEmpty()) {
            this.fullPrice = this.fullPrice.subtract(elemIfExist.get().getPrice());
            this.element.remove(elemIfExist.get());
        }
    }
    
    public void setFullPrice(String fullPrice){
        this.fullPrice = new BigDecimal(fullPrice);
    }
}
