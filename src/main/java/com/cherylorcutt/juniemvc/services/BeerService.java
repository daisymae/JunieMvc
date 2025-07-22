package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.models.BeerDto;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for Beer operations
 */
public interface BeerService {
    
    /**
     * Save a new beer or update an existing one
     * 
     * @param beerDto the beer DTO to save
     * @return the saved beer DTO
     */
    BeerDto saveBeer(BeerDto beerDto);
    
    /**
     * Get a beer by its ID
     * 
     * @param id the beer ID
     * @return an Optional containing the beer DTO if found, or empty if not found
     */
    Optional<BeerDto> getBeerById(Integer id);
    
    /**
     * Get all beers
     * 
     * @return a list of all beer DTOs
     */
    List<BeerDto> getAllBeers();

    /**
     * Delete a beer by its ID
     * 
     * @param id the beer ID
     * @return true if the beer was deleted, false if it was not found
     */
    boolean deleteBeerById(Integer id);
}