# Detailed Plan for Adding DTOs to the Beer API

## Overview
This document outlines the detailed plan for implementing Data Transfer Objects (DTOs) in the Beer API to separate the web layer from the persistence layer. The implementation will follow the requirements specified in `/prompts/add-dtos/requirements.md` and adhere to Spring Boot best practices.

## Current Project Structure
The current implementation directly exposes JPA entities in the REST API:
- `Beer` entity is used in controller methods as both request and response objects
- `BeerService` interface and implementation work directly with the `Beer` entity
- `BeerRepository` provides data access for the `Beer` entity
- Tests are written to work with the `Beer` entity

## Implementation Plan

### 1. Create DTO Classes
1. Create a new package `com.cherylorcutt.juniemvc.models`
2. Create `BeerDto` class with the following:
   - Fields: id, version, beerName, beerStyle, upc, quantityOnHand, price
   - Lombok annotations: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
   - Jakarta Validation annotations:
     - `@NotBlank` for beerName, beerStyle, upc
     - `@NotNull` for price, quantityOnHand
     - `@PositiveOrZero` for quantityOnHand
     - `@Positive` for price
   - Note: createdDate and updateDate will not be included in the DTO

### 2. Create MapStruct Mapper
1. Create a new package `com.cherylorcutt.juniemvc.mappers`
2. Create `BeerMapper` interface with MapStruct:
   ```java
   @Mapper(componentModel = "spring")
   public interface BeerMapper {
       BeerDto beerToBeerDto(Beer beer);
       
       @Mapping(target = "id", ignore = true)
       @Mapping(target = "createdDate", ignore = true)
       @Mapping(target = "updateDate", ignore = true)
       Beer beerDtoToBeer(BeerDto beerDto);
   }
   ```

### 3. Update Service Layer
1. Modify `BeerService` interface to use DTOs:
   ```java
   public interface BeerService {
       BeerDto saveBeer(BeerDto beerDto);
       Optional<BeerDto> getBeerById(Integer id);
       List<BeerDto> getAllBeers();
       boolean deleteBeerById(Integer id);
   }
   ```

2. Update `BeerServiceImpl` to:
   - Inject `BeerMapper`
   - Convert between DTOs and entities
   - Add `@Transactional` annotations:
     - `@Transactional(readOnly = true)` for query methods
     - `@Transactional` for data-modifying methods

### 4. Update Controller Layer
1. Update `BeerController` to:
   - Use `BeerDto` for all request and response objects
   - Add `@Valid` annotation for request validation
   - Maintain the same REST API endpoints and HTTP status codes

### 5. Update Tests
1. Update `BeerControllerTest`:
   - Create `BeerDto` objects instead of `Beer` entities
   - Update mock service method expectations to use DTOs
   - Update assertions to match DTO structure
   - Add tests for validation errors

2. Update `BeerServiceTest`:
   - Create `BeerDto` objects for input
   - Update mock repository expectations
   - Update assertions to verify DTOs

### 6. Testing and Verification
1. Run all tests to ensure they pass
2. Verify that the API continues to function as before from a client perspective
3. Verify that input validation is properly implemented
4. Verify that the web layer is properly decoupled from the persistence layer

## Implementation Details

### BeerDto Class
```java
package com.cherylorcutt.juniemvc.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerDto {
    private Integer id;
    private Integer version;
    
    @NotBlank(message = "Beer name is required")
    private String beerName;
    
    @NotBlank(message = "Beer style is required")
    private String beerStyle;
    
    @NotBlank(message = "UPC is required")
    private String upc;
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;
    
    @NotNull(message = "Quantity on hand is required")
    @PositiveOrZero(message = "Quantity on hand must be zero or positive")
    private Integer quantityOnHand;
}
```

### BeerMapper Interface
```java
package com.cherylorcutt.juniemvc.mappers;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.models.BeerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BeerMapper {
    BeerDto beerToBeerDto(Beer beer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Beer beerDtoToBeer(BeerDto beerDto);
}
```

### Updated BeerService Interface
```java
package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.models.BeerDto;

import java.util.List;
import java.util.Optional;

public interface BeerService {
    BeerDto saveBeer(BeerDto beerDto);
    Optional<BeerDto> getBeerById(Integer id);
    List<BeerDto> getAllBeers();
    boolean deleteBeerById(Integer id);
}
```

### Updated BeerServiceImpl
```java
package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.mappers.BeerMapper;
import com.cherylorcutt.juniemvc.models.BeerDto;
import com.cherylorcutt.juniemvc.repositories.BeerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BeerServiceImpl implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    public BeerServiceImpl(BeerRepository beerRepository, BeerMapper beerMapper) {
        this.beerRepository = beerRepository;
        this.beerMapper = beerMapper;
    }

    @Override
    @Transactional
    public BeerDto saveBeer(BeerDto beerDto) {
        Beer beer = beerMapper.beerDtoToBeer(beerDto);
        Beer savedBeer = beerRepository.save(beer);
        return beerMapper.beerToBeerDto(savedBeer);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BeerDto> getBeerById(Integer id) {
        return beerRepository.findById(id)
                .map(beerMapper::beerToBeerDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeerDto> getAllBeers() {
        return beerRepository.findAll().stream()
                .map(beerMapper::beerToBeerDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean deleteBeerById(Integer id) {
        if (beerRepository.existsById(id)) {
            beerRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
```

### Updated BeerController
```java
package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.models.BeerDto;
import com.cherylorcutt.juniemvc.services.BeerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/beers")
public class BeerController {

    private final BeerService beerService;

    public BeerController(BeerService beerService) {
        this.beerService = beerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BeerDto createBeer(@Valid @RequestBody BeerDto beerDto) {
        return beerService.saveBeer(beerDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BeerDto> getBeerById(@PathVariable Integer id) {
        Optional<BeerDto> beer = beerService.getBeerById(id);
        return beer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<BeerDto> getAllBeers() {
        return beerService.getAllBeers();
    }

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
```

## Conclusion
This plan provides a comprehensive approach to implementing DTOs in the Beer API. By following this plan, we will achieve a clear separation between the web layer and the persistence layer, ensure proper validation of incoming data, maintain backward compatibility with existing API consumers, and follow Spring Boot best practices for API design.