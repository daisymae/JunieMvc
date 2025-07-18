package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.services.BeerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
@Import(BeerControllerTest.BeerServiceConfig.class)
class BeerControllerTest {

    @TestConfiguration
    static class BeerServiceConfig {
        @Bean
        public BeerService beerService() {
            return Mockito.mock(BeerService.class);
        }
    }

    @Autowired
    private BeerService beerService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Beer testBeer;
    private List<Beer> testBeers;

    @BeforeEach
    void setUp() {
        testBeer = Beer.builder()
                .id(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .upc("123456")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(100)
                .build();

        Beer testBeer2 = Beer.builder()
                .id(2)
                .beerName("Another Beer")
                .beerStyle("Stout")
                .upc("654321")
                .price(new BigDecimal("14.99"))
                .quantityOnHand(200)
                .build();

        testBeers = Arrays.asList(testBeer, testBeer2);
    }

    @Test
    void testCreateBeer() throws Exception {
        // Given
        Beer beerToCreate = Beer.builder()
                .beerName("New Beer")
                .beerStyle("Lager")
                .upc("789012")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(50)
                .build();

        given(beerService.saveBeer(any(Beer.class))).willReturn(
                Beer.builder()
                        .id(3)
                        .beerName("New Beer")
                        .beerStyle("Lager")
                        .upc("789012")
                        .price(new BigDecimal("10.99"))
                        .quantityOnHand(50)
                        .build()
        );

        // When/Then
        mockMvc.perform(post("/api/v1/beers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.beerName", is("New Beer")))
                .andExpect(jsonPath("$.beerStyle", is("Lager")))
                .andExpect(jsonPath("$.upc", is("789012")))
                .andExpect(jsonPath("$.price", is(10.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(50)));

        verify(beerService).saveBeer(any(Beer.class));
    }

    @Test
    void testGetBeerById() throws Exception {
        // Given
        given(beerService.getBeerById(1)).willReturn(Optional.of(testBeer));

        // When/Then
        mockMvc.perform(get("/api/v1/beers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.beerName", is("Test Beer")))
                .andExpect(jsonPath("$.beerStyle", is("IPA")))
                .andExpect(jsonPath("$.upc", is("123456")))
                .andExpect(jsonPath("$.price", is(12.99)))
                .andExpect(jsonPath("$.quantityOnHand", is(100)));

        verify(beerService).getBeerById(1);
    }

    @Test
    void testGetBeerByIdNotFound() throws Exception {
        // Given
        given(beerService.getBeerById(999)).willReturn(Optional.empty());

        // When/Then
        mockMvc.perform(get("/api/v1/beers/999"))
                .andExpect(status().isNotFound());

        verify(beerService).getBeerById(999);
    }

    @Test
    void testGetAllBeers() throws Exception {
        // Given
        given(beerService.getAllBeers()).willReturn(testBeers);

        // When/Then
        mockMvc.perform(get("/api/v1/beers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].beerName", is("Test Beer")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].beerName", is("Another Beer")));

        verify(beerService).getAllBeers();
    }
}