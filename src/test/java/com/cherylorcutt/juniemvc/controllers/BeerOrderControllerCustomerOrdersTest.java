package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.exceptions.GlobalExceptionHandler;
import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.BeerOrderLineDto;
import com.cherylorcutt.juniemvc.services.BeerOrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BeerOrderControllerCustomerOrdersTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private BeerOrderService beerOrderService;
    
    @InjectMocks
    private BeerOrderController beerOrderController;
    
    @BeforeEach
    void setUp() {
        // Initialize MockMvc with controller and exception handler
        mockMvc = MockMvcBuilders.standaloneSetup(beerOrderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getBeerOrdersByCustomerId_ShouldReturnCustomerOrders() throws Exception {
        // given
        BeerOrderLineDto lineDto = BeerOrderLineDto.builder()
                .id(1)
                .beerId(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .orderQuantity(10)
                .build();

        BeerOrderDto beerOrderDto = BeerOrderDto.builder()
                .id(1)
                .customerId(1)
                .orderStatus("NEW")
                .beerOrderLines(Collections.singletonList(lineDto))
                .createdDate(LocalDateTime.now())
                .build();

        List<BeerOrderDto> orders = Collections.singletonList(beerOrderDto);
        
        // Use specific value 1 instead of anyInt()
        when(beerOrderService.getBeerOrdersByCustomerId(1)).thenReturn(orders);

        // when/then
        mockMvc.perform(get("/api/v1/orders/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].customerId", is(1)))
                .andExpect(jsonPath("$[0].orderStatus", is("NEW")));
    }
}