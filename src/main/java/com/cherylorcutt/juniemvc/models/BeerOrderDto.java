package com.cherylorcutt.juniemvc.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for BeerOrder operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderDto {
    private Integer id;
    private Integer version;
    private String orderStatus;
    private String orderStatusCallbackUrl;
    
    @NotNull(message = "Customer ID is required")
    private Integer customerId;
    
    @Valid
    private List<BeerOrderLineDto> beerOrderLines;
    
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}