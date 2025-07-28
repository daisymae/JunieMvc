package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.models.CustomerDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerDto saveCustomer(CustomerDto customerDto);
    Optional<CustomerDto> getCustomerById(Integer id);
    Optional<CustomerDto> getCustomerByEmail(String email);
    List<CustomerDto> getAllCustomers();
    boolean deleteCustomerById(Integer id);
}