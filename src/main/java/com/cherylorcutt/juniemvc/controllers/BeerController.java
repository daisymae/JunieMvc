package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.services.BeerService;
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
     * @param beer the beer to create
     * @return the created beer with HTTP status 201 (CREATED)
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Beer createBeer(@RequestBody Beer beer) {
        return beerService.saveBeer(beer);
    }

    /**
     * Get a beer by its ID
     * 
     * @param id the beer ID
     * @return the beer if found with HTTP status 200 (OK), or HTTP status 404 (NOT_FOUND) if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Beer> getBeerById(@PathVariable Integer id) {
        Optional<Beer> beer = beerService.getBeerById(id);
        return beer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get all beers
     * 
     * @return a list of all beers with HTTP status 200 (OK)
     */
    @GetMapping
    public List<Beer> getAllBeers() {
        return beerService.getAllBeers();
    }
}