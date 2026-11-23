package com.inteview.orderprocessingservice.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(Long id){
        super("Order not found with Id: " +id);
    }

}
