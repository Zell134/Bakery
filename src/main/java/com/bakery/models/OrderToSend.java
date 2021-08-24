package com.bakery.models;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderToSend implements Serializable {
    
    static final long serialVersionUID = 1L;

    private long id;
    private LocalDateTime orderDate;
    private BigDecimal fullPrice;
    private String destination;
    private boolean isNew;
    
    public void convertFromOrder(Order order){
        this.id = order.getId();
        this.orderDate = order.getOrderDate();
        this.fullPrice = order.getFullPrice();
        this.destination = order.getDestination();
        this.isNew = order.isNew();
    }
    
}
