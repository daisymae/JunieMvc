package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.entities.Beer;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Beer operations
 */
public interface BeerService {
    
    /**
     * Save a new beer or update an existing one
     * 
     * @param beer the beer to save
     * @return the saved beer
     */
    Beer saveBeer(Beer beer);
    
    /**
     * Get a beer by its ID
     * 
     * @param id the beer ID
     * @return an Optional containing the beer if found, or empty if not found
     */
    Optional<Beer> getBeerById(Integer id);
    
    /**
     * Get all beers
     * 
     * @return a list of all beers
     */
    List<Beer> getAllBeers();

    /**
     * Delete a beer by its ID
     * 
     * @param id the beer ID
     * @return true if the beer was deleted, false if it was not found
     */
    boolean deleteBeerById(Integer id);
}