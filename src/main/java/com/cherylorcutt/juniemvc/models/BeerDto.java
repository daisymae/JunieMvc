package com.cherylorcutt.juniemvc.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Data Transfer Object for Beer operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
    private Integer id;
    private Integer version;
    
    @NotBlank(message = "Beer name is required")
    private String beerName;
    
    @NotBlank(message = "Beer style is required")
    private String beerStyle;
    
    @NotBlank(message = "UPC is required")
    private String upc;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Quantity on hand is required")
    @PositiveOrZero(message = "Quantity on hand must be zero or positive")
    private Integer quantityOnHand;
}