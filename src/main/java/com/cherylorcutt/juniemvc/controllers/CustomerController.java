package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.models.CustomerDto;
import com.cherylorcutt.juniemvc.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {

    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CustomerDto createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        return customerService.saveCustomer(customerDto);
    }

    @GetMapping("/{id}")
    ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        Optional<CustomerDto> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    ResponseEntity<CustomerDto> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerDto customerDto) {
        Optional<CustomerDto> existingCustomer = customerService.getCustomerById(id);
        if (existingCustomer.isPresent()) {
            customerDto.setId(id);
            CustomerDto updatedCustomer = customerService.saveCustomer(customerDto);
            return ResponseEntity.ok(updatedCustomer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        boolean deleted = customerService.deleteCustomerById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}