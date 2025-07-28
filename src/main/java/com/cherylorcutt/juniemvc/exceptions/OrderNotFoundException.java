package com.cherylorcutt.juniemvc.exceptions;

public class OrderNotFoundException extends RuntimeException {
    
    public OrderNotFoundException(String message) {
        super(message);
    }
    
    public OrderNotFoundException(Integer id) {
        super("Order not found with ID: " + id);
    }
}