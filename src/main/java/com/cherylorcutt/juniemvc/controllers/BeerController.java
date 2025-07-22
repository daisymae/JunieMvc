package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.models.BeerDto;
import com.cherylorcutt.juniemvc.services.BeerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Beer operations
 */
@RestController
@RequestMapping("/api/v1/beers")
public class BeerController {

    private final BeerService beerService;

    /**
     * Constructor injection of the BeerService
     * 
     * @param beerService the beer service
     */
    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    /**
     * Create a new beer
     * 
     * @param beerDto the beer DTO to create
     * @return the created beer DTO with HTTP status 201 (CREATED)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDto createBeer(@Valid @RequestBody BeerDto beerDto) {
        return beerService.saveBeer(beerDto);
    }

    /**
     * Get a beer by its ID
     * 
     * @param id the beer ID
     * @return the beer DTO if found with HTTP status 200 (OK), or HTTP status 404 (NOT_FOUND) if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable Integer id) {
        Optional<BeerDto> beer = beerService.getBeerById(id);
        return beer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all beers
     * 
     * @return a list of all beer DTOs with HTTP status 200 (OK)
     */
    @GetMapping
    public List<BeerDto> getAllBeers() {
        return beerService.getAllBeers();
    }

    /**
     * Update an existing beer
     * 
     * @param id the beer ID
     * @param beerDto the updated beer DTO data
     * @return the updated beer DTO if found with HTTP status 200 (OK), or HTTP status 404 (NOT_FOUND) if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<BeerDto> updateBeer(@PathVariable Integer id, @Valid @RequestBody BeerDto beerDto) {
        Optional<BeerDto> existingBeer = beerService.getBeerById(id);
        if (existingBeer.isPresent()) {
            beerDto.setId(id);
            BeerDto updatedBeer = beerService.saveBeer(beerDto);
            return ResponseEntity.ok(updatedBeer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a beer by its ID
     * 
     * @param id the beer ID
     * @return HTTP status 204 (NO_CONTENT) if deleted, or HTTP status 404 (NOT_FOUND) if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBeer(@PathVariable Integer id) {
        boolean deleted = beerService.deleteBeerById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}