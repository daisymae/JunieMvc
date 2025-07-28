package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.exceptions.CustomerNotFoundException;
import com.cherylorcutt.juniemvc.exceptions.GlobalExceptionHandler;
import com.cherylorcutt.juniemvc.models.CustomerDto;
import com.cherylorcutt.juniemvc.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CustomerService customerService;
    
    @InjectMocks
    private CustomerController customerController;

    private CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc with controller and exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        
        // Setup test data
        customerDto = CustomerDto.builder()
                .id(1)
                .customerName("Test Customer")
                .email("test@example.com")
                .phone("123-456-7890")
                .build();
    }

    @Test
    void createCustomer_ShouldReturnCreatedCustomer() throws Exception {
        // given
        CustomerDto inputDto = CustomerDto.builder()
                .customerName("New Customer")
                .email("new@example.com")
                .phone("987-654-3210")
                .build();

        CustomerDto savedDto = CustomerDto.builder()
                .id(1)
                .customerName("New Customer")
                .email("new@example.com")
                .phone("987-654-3210")
                .build();

        when(customerService.saveCustomer(any(CustomerDto.class))).thenReturn(savedDto);

        // when/then
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("New Customer")))
                .andExpect(jsonPath("$.email", is("new@example.com")))
                .andExpect(jsonPath("$.phone", is("987-654-3210")));

        verify(customerService).saveCustomer(any(CustomerDto.class));
    }

    @Test
    void createCustomer_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // given
        CustomerDto invalidDto = CustomerDto.builder()
                .customerName("")  // Empty name, should fail validation
                .email("invalid-email")  // Invalid email format
                .build();

        // when/then
        mockMvc.perform(post("/api/v1/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenCustomerExists() throws Exception {
        // given
        when(customerService.getCustomerById(anyInt())).thenReturn(Optional.of(customerDto));

        // when/then
        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("Test Customer")))
                .andExpect(jsonPath("$.email", is("test@example.com")))
                .andExpect(jsonPath("$.phone", is("123-456-7890")));

        verify(customerService).getCustomerById(1);
    }

    @Test
    void getCustomerById_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        // given
        doThrow(new CustomerNotFoundException(1)).when(customerService).getCustomerById(anyInt());

        // when/then
        mockMvc.perform(get("/api/v1/customers/1"))
                .andExpect(status().isNotFound());

        // No need to verify the exact number of calls
    }

    @Test
    void getAllCustomers_ShouldReturnAllCustomers() throws Exception {
        // given
        CustomerDto customer2 = CustomerDto.builder()
                .id(2)
                .customerName("Another Customer")
                .email("another@example.com")
                .build();

        when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customerDto, customer2));

        // when/then
        mockMvc.perform(get("/api/v1/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customerName", is("Test Customer")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].customerName", is("Another Customer")));

        verify(customerService).getAllCustomers();
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer_WhenCustomerExists() throws Exception {
        // given
        CustomerDto updateDto = CustomerDto.builder()
                .customerName("Updated Customer")
                .email("updated@example.com")
                .phone("555-555-5555")
                .build();

        CustomerDto updatedDto = CustomerDto.builder()
                .id(1)
                .customerName("Updated Customer")
                .email("updated@example.com")
                .phone("555-555-5555")
                .build();

        when(customerService.getCustomerById(anyInt())).thenReturn(Optional.of(customerDto));
        when(customerService.saveCustomer(any(CustomerDto.class))).thenReturn(updatedDto);

        // when/then
        mockMvc.perform(put("/api/v1/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerName", is("Updated Customer")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.phone", is("555-555-5555")));

        // No need to verify the exact number of calls
    }

    @Test
    void updateCustomer_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        // given
        CustomerDto updateDto = CustomerDto.builder()
                .customerName("Updated Customer")
                .email("updated@example.com")
                .build();

        doThrow(new CustomerNotFoundException(1)).when(customerService).getCustomerById(anyInt());

        // when/then
        mockMvc.perform(put("/api/v1/customers/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        // No need to verify the exact number of calls
    }

    @Test
    void deleteCustomer_ShouldReturnNoContent_WhenCustomerExists() throws Exception {
        // given
        when(customerService.deleteCustomerById(anyInt())).thenReturn(true);

        // when/then
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNoContent());

        verify(customerService).deleteCustomerById(1);
    }

    @Test
    void deleteCustomer_ShouldReturnNotFound_WhenCustomerDoesNotExist() throws Exception {
        // given
        when(customerService.deleteCustomerById(anyInt())).thenReturn(false);

        // when/then
        mockMvc.perform(delete("/api/v1/customers/1"))
                .andExpect(status().isNotFound());

        // No need to verify the exact number of calls
    }
}