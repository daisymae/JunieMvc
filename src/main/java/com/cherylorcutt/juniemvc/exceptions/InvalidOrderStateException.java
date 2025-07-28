package com.cherylorcutt.juniemvc.exceptions;

import com.cherylorcutt.juniemvc.entities.BeerOrder.OrderStatus;

public class InvalidOrderStateException extends RuntimeException {
    
    private final OrderStatus currentStatus;
    private final OrderStatus targetStatus;
    
    public InvalidOrderStateException(String message) {
        super(message);
        this.currentStatus = null;
        this.targetStatus = null;
    }
    
    public InvalidOrderStateException(OrderStatus currentStatus, OrderStatus targetStatus) {
        super("Cannot transition order from " + currentStatus + " to " + targetStatus);
        this.currentStatus = currentStatus;
        this.targetStatus = targetStatus;
    }
    
    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }
    
    public OrderStatus getTargetStatus() {
        return targetStatus;
    }
}