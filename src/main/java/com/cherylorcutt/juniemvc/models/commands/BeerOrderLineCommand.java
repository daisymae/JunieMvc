package com.cherylorcutt.juniemvc.models.commands;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Command object for beer order line items
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderLineCommand {
    
    @NotNull(message = "Beer ID is required")
    private Integer beerId;
    
    @NotNull(message = "Order quantity is required")
    @Positive(message = "Order quantity must be positive")
    private Integer orderQuantity;
}