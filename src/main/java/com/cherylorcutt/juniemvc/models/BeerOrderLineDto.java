package com.cherylorcutt.juniemvc.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for BeerOrderLine operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderLineDto {
    private Integer id;
    private Integer version;
    
    @NotNull(message = "Order quantity is required")
    @Positive(message = "Order quantity must be positive")
    private Integer orderQuantity;
    
    @NotNull(message = "Beer ID is required")
    private Integer beerId;
    
    private String beerName;
    private String beerStyle;
    private String upc;
}