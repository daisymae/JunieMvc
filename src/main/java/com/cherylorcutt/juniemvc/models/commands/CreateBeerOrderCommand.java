package com.cherylorcutt.juniemvc.models.commands;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Command object for creating a beer order
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBeerOrderCommand {
    
    @NotNull(message = "Customer ID is required")
    private Integer customerId;
    
    @NotEmpty(message = "Order must contain at least one beer")
    @Valid
    private List<BeerOrderLineCommand> beerOrderLines;
    
    private String orderStatusCallbackUrl;
}