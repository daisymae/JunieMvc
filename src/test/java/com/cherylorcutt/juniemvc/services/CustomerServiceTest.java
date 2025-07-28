package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.exceptions.CustomerNotFoundException;
import com.cherylorcutt.juniemvc.mappers.CustomerMapper;
import com.cherylorcutt.juniemvc.models.CustomerDto;
import com.cherylorcutt.juniemvc.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CustomerMapper customerMapper;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer customer;
    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1)
                .customerName("Test Customer")
                .email("test@example.com")
                .phone("123-456-7890")
                .build();

        customerDto = CustomerDto.builder()
                .id(1)
                .customerName("Test Customer")
                .email("test@example.com")
                .phone("123-456-7890")
                .build();
    }

    @Test
    void saveCustomer_ShouldReturnSavedCustomerDto() {
        // given
        when(customerMapper.customerDtoToCustomer(any(CustomerDto.class))).thenReturn(customer);
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);
        when(customerMapper.customerToCustomerDto(any(Customer.class))).thenReturn(customerDto);

        // when
        CustomerDto saved = customerService.saveCustomer(customerDto);

        // then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isEqualTo(1);
        assertThat(saved.getCustomerName()).isEqualTo("Test Customer");
        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void getCustomerById_ShouldReturnCustomerDto_WhenCustomerExists() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(customerMapper.customerToCustomerDto(any(Customer.class))).thenReturn(customerDto);

        // when
        Optional<CustomerDto> found = customerService.getCustomerById(1);

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(1);
        assertThat(found.get().getCustomerName()).isEqualTo("Test Customer");
    }

    @Test
    void getCustomerById_ShouldThrowException_WhenCustomerDoesNotExist() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> customerService.getCustomerById(1))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with ID: 1");
    }

    @Test
    void getCustomerByEmail_ShouldReturnCustomerDto_WhenEmailExists() {
        // given
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(customer));
        when(customerMapper.customerToCustomerDto(any(Customer.class))).thenReturn(customerDto);

        // when
        Optional<CustomerDto> found = customerService.getCustomerByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    void getCustomerByEmail_ShouldThrowException_WhenEmailDoesNotExist() {
        // given
        when(customerRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> customerService.getCustomerByEmail("nonexistent@example.com"))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with email: nonexistent@example.com");
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() {
        // given
        Customer customer2 = Customer.builder()
                .id(2)
                .customerName("Another Customer")
                .email("another@example.com")
                .build();
        
        CustomerDto customerDto2 = CustomerDto.builder()
                .id(2)
                .customerName("Another Customer")
                .email("another@example.com")
                .build();

        when(customerRepository.findAll()).thenReturn(Arrays.asList(customer, customer2));
        when(customerMapper.customerToCustomerDto(customer)).thenReturn(customerDto);
        when(customerMapper.customerToCustomerDto(customer2)).thenReturn(customerDto2);

        // when
        List<CustomerDto> customers = customerService.getAllCustomers();

        // then
        assertThat(customers).hasSize(2);
        assertThat(customers).extracting(CustomerDto::getId).containsExactly(1, 2);
    }

    @Test
    void deleteCustomerById_ShouldReturnTrue_WhenCustomerExists() {
        // given
        when(customerRepository.existsById(anyInt())).thenReturn(true);

        // when
        boolean result = customerService.deleteCustomerById(1);

        // then
        assertThat(result).isTrue();
        verify(customerRepository).deleteById(1);
    }

    @Test
    void deleteCustomerById_ShouldReturnFalse_WhenCustomerDoesNotExist() {
        // given
        when(customerRepository.existsById(anyInt())).thenReturn(false);

        // when
        boolean result = customerService.deleteCustomerById(1);

        // then
        assertThat(result).isFalse();
    }
}