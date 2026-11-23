package com.inteview.orderprocessingservice.model;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

@Entity
@Table(name = "orders")
public class Order {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Column(nullable = false)
    private String customerId;

    @Getter
    @Column(nullable = false)
    private String product;

    @Getter
    @Column(nullable = false)
    private int quantity;

    @Getter
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal price;

    @Getter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Getter
    @Column(nullable = false)
    private Instant createdAt;

    @Getter
    private Instant updatedAt;

    protected Order(){
    }

    public Order(String customerId, String product, int quantity, BigDecimal price){
        this.customerId = customerId;
        this.product = product;
        this.quantity = quantity;
        this.price = price;
        this.status = OrderStatus.CREATED;
        this.createdAt = Instant.now();
    }

    public void changeStatus(OrderStatus newStatus){
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

}
