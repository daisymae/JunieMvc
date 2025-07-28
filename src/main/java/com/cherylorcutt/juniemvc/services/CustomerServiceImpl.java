package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.exceptions.CustomerNotFoundException;
import com.cherylorcutt.juniemvc.mappers.CustomerMapper;
import com.cherylorcutt.juniemvc.models.CustomerDto;
import com.cherylorcutt.juniemvc.repositories.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
    
    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    CustomerServiceImpl(CustomerRepository customerRepository, CustomerMapper customerMapper) {
        this.customerRepository = customerRepository;
        this.customerMapper = customerMapper;
    }

    @Override
    @Transactional
    public CustomerDto saveCustomer(CustomerDto customerDto) {
        log.debug("Saving customer: {}", customerDto.getCustomerName());
        Customer customer = customerMapper.customerDtoToCustomer(customerDto);
        Customer savedCustomer = customerRepository.save(customer);
        return customerMapper.customerToCustomerDto(savedCustomer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDto> getCustomerById(Integer id) {
        log.debug("Getting customer by ID: {}", id);
        return Optional.ofNullable(customerRepository.findById(id)
                .map(customerMapper::customerToCustomerDto)
                .orElseThrow(() -> new CustomerNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDto> getCustomerByEmail(String email) {
        log.debug("Getting customer by email: {}", email);
        return Optional.ofNullable(customerRepository.findByEmail(email)
                .map(customerMapper::customerToCustomerDto)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with email: " + email)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CustomerDto> getAllCustomers() {
        log.debug("Getting all customers");
        return customerRepository.findAll().stream()
                .map(customerMapper::customerToCustomerDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteCustomerById(Integer id) {
        log.debug("Deleting customer by ID: {}", id);
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}