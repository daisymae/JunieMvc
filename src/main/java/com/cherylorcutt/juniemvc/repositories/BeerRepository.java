package com.cherylorcutt.juniemvc.repositories;

import com.cherylorcutt.juniemvc.entities.Beer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BeerRepository extends JpaRepository<Beer, Integer> {
    // JpaRepository provides basic CRUD operations
    // The generic types are: <Entity type, ID type>
}