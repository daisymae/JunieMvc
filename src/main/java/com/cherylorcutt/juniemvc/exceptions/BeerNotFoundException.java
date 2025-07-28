package com.cherylorcutt.juniemvc.exceptions;

public class BeerNotFoundException extends RuntimeException {
    
    public BeerNotFoundException(String message) {
        super(message);
    }
    
    public BeerNotFoundException(Integer id) {
        super("Beer not found with ID: " + id);
    }
}