package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.repositories.BeerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the BeerService interface
 */
@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;

    /**
     * Constructor injection of the BeerRepository
     * 
     * @param beerRepository the beer repository
     */
    public BeerServiceImpl(BeerRepository beerRepository) {
        this.beerRepository = beerRepository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Beer saveBeer(Beer beer) {
        return beerRepository.save(beer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Beer> getBeerById(Integer id) {
        return beerRepository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Beer> getAllBeers() {
        return beerRepository.findAll();
    }
}