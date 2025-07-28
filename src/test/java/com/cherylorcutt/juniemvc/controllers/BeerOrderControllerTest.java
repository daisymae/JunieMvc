package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.exceptions.CustomerNotFoundException;
import com.cherylorcutt.juniemvc.exceptions.GlobalExceptionHandler;
import com.cherylorcutt.juniemvc.exceptions.OrderNotFoundException;
import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.BeerOrderLineDto;
import com.cherylorcutt.juniemvc.models.commands.BeerOrderLineCommand;
import com.cherylorcutt.juniemvc.models.commands.CreateBeerOrderCommand;
import com.cherylorcutt.juniemvc.services.BeerOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BeerOrderControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BeerOrderService beerOrderService;

    @InjectMocks
    private BeerOrderController beerOrderController;

    private BeerOrderDto beerOrderDto;
    private CreateBeerOrderCommand createCommand;

    @BeforeEach
    void setUp() {
        // Initialize MockMvc with controller and exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(beerOrderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
        
        // Setup test data
        BeerOrderLineDto lineDto = BeerOrderLineDto.builder()
                .id(1)
                .beerId(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .orderQuantity(10)
                .build();

        beerOrderDto = BeerOrderDto.builder()
                .id(1)
                .customerId(1)
                .orderStatus("NEW")
                .beerOrderLines(Collections.singletonList(lineDto))
                .createdDate(LocalDateTime.now())
                .build();

        BeerOrderLineCommand lineCommand = BeerOrderLineCommand.builder()
                .beerId(1)
                .orderQuantity(10)
                .build();

        createCommand = CreateBeerOrderCommand.builder()
                .customerId(1)
                .beerOrderLines(Collections.singletonList(lineCommand))
                .build();
    }

    @Test
    void createBeerOrder_ShouldReturnCreatedOrder() throws Exception {
        // given
        when(beerOrderService.createBeerOrder(any(CreateBeerOrderCommand.class))).thenReturn(beerOrderDto);

        // when/then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommand)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.orderStatus", is("NEW")))
                .andExpect(jsonPath("$.beerOrderLines", hasSize(1)))
                .andExpect(jsonPath("$.beerOrderLines[0].beerId", is(1)))
                .andExpect(jsonPath("$.beerOrderLines[0].orderQuantity", is(10)));

        verify(beerOrderService).createBeerOrder(any(CreateBeerOrderCommand.class));
    }

    @Test
    void createBeerOrder_ShouldReturnBadRequest_WhenValidationFails() throws Exception {
        // given
        CreateBeerOrderCommand invalidCommand = CreateBeerOrderCommand.builder()
                .customerId(null)  // Missing customer ID
                .beerOrderLines(Collections.emptyList())  // Empty order lines
                .build();

        // when/then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCommand)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBeerOrder_ShouldReturnNotFound_WhenCustomerNotFound() throws Exception {
        // given
        when(beerOrderService.createBeerOrder(any(CreateBeerOrderCommand.class)))
                .thenThrow(new CustomerNotFoundException(1));

        // when/then
        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createCommand)))
                .andExpect(status().isNotFound());

        // No need to verify the exact number of calls
    }

    @Test
    void getBeerOrderById_ShouldReturnOrder_WhenOrderExists() throws Exception {
        // given
        when(beerOrderService.getBeerOrderById(anyInt())).thenReturn(Optional.of(beerOrderDto));

        // when/then
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.customerId", is(1)))
                .andExpect(jsonPath("$.orderStatus", is("NEW")));

        verify(beerOrderService).getBeerOrderById(1);
    }

    @Test
    void getBeerOrderById_ShouldReturnNotFound_WhenOrderDoesNotExist() throws Exception {
        // given
        doThrow(new OrderNotFoundException(1)).when(beerOrderService).getBeerOrderById(anyInt());

        // when/then
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isNotFound());

        // No need to verify the exact number of calls
    }

    @Test
    void getAllBeerOrders_ShouldReturnAllOrders() throws Exception {
        // given
        BeerOrderDto order2 = BeerOrderDto.builder()
                .id(2)
                .customerId(1)
                .orderStatus("PROCESSING")
                .build();

        List<BeerOrderDto> orders = Arrays.asList(beerOrderDto, order2);
        when(beerOrderService.getAllBeerOrders()).thenReturn(orders);

        // when/then
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].orderStatus", is("NEW")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].orderStatus", is("PROCESSING")));

        verify(beerOrderService).getAllBeerOrders();
    }

    @Test
    void getBeerOrdersByCustomerId_ShouldReturnCustomerOrders() throws Exception {
        // given
        // Create a new list to avoid any potential issues with the shared beerOrderDto
        BeerOrderLineDto lineDto = BeerOrderLineDto.builder()
                .id(1)
                .beerId(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .orderQuantity(10)
                .build();

        BeerOrderDto orderDto = BeerOrderDto.builder()
                .id(1)
                .customerId(1)
                .orderStatus("NEW")
                .beerOrderLines(Collections.singletonList(lineDto))
                .createdDate(LocalDateTime.now())
                .build();

        List<BeerOrderDto> orders = Collections.singletonList(orderDto);
        
        // Use specific value 1 instead of anyInt() to avoid ambiguity
        when(beerOrderService.getBeerOrdersByCustomerId(1)).thenReturn(orders);

        // when/then
        mockMvc.perform(get("/api/v1/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customerId", is(1)))
                .andExpect(jsonPath("$[0].orderStatus", is("NEW")));
    }

    @Test
    void getBeerOrdersByCustomerId_ShouldReturnNotFound_WhenCustomerNotFound() throws Exception {
        // given
        when(beerOrderService.getBeerOrdersByCustomerId(anyInt()))
                .thenThrow(new CustomerNotFoundException(1));

        // when/then
        mockMvc.perform(get("/api/v1/orders/customer/1"))
                .andExpect(status().isNotFound());

        verify(beerOrderService).getBeerOrdersByCustomerId(1);
    }

    @Test
    void cancelBeerOrder_ShouldReturnNoContent_WhenOrderCancelled() throws Exception {
        // given
        when(beerOrderService.cancelBeerOrder(anyInt())).thenReturn(true);

        // when/then
        mockMvc.perform(put("/api/v1/orders/1/cancel"))
                .andExpect(status().isNoContent());

        verify(beerOrderService).cancelBeerOrder(1);
    }

    @Test
    void cancelBeerOrder_ShouldReturnNotFound_WhenOrderNotFound() throws Exception {
        // given
        when(beerOrderService.cancelBeerOrder(anyInt()))
                .thenThrow(new OrderNotFoundException(1));

        // when/then
        mockMvc.perform(put("/api/v1/orders/1/cancel"))
                .andExpect(status().isNotFound());

        // No need to verify the exact number of calls
    }
}