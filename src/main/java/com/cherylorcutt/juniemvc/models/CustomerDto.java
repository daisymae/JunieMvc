package com.cherylorcutt.juniemvc.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Customer operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Integer id;
    private Integer version;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private String phone;
}