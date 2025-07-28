package com.cherylorcutt.juniemvc.exceptions;

public class CustomerNotFoundException extends RuntimeException {
    
    public CustomerNotFoundException(String message) {
        super(message);
    }
    
    public CustomerNotFoundException(Integer id) {
        super("Customer not found with ID: " + id);
    }
}