# Task List for Adding DTOs to the Beer API

## 1. Create DTO Classes
- [x] Create a new package `com.cherylorcutt.juniemvc.models`
- [x] Create `BeerDto` class with the following:
  - [x] Add fields: id, version, beerName, beerStyle, upc, quantityOnHand, price
  - [x] Add Lombok annotations: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
  - [x] Add Jakarta Validation annotations:
    - [x] `@NotBlank` for beerName, beerStyle, upc
    - [x] `@NotNull` for price, quantityOnHand
    - [x] `@PositiveOrZero` for quantityOnHand
    - [x] `@Positive` for price

## 2. Create MapStruct Mapper
- [x] Add MapStruct dependency to pom.xml
- [x] Create a new package `com.cherylorcutt.juniemvc.mappers`
- [x] Create `BeerMapper` interface with MapStruct:
  - [x] Add `@Mapper(componentModel = "spring")` annotation
  - [x] Add method to convert Beer to BeerDto: `BeerDto beerToBeerDto(Beer beer)`
  - [x] Add method to convert BeerDto to Beer with mappings:
    - [x] Add `@Mapping(target = "id", ignore = true)`
    - [x] Add `@Mapping(target = "createdDate", ignore = true)`
    - [x] Add `@Mapping(target = "updateDate", ignore = true)`

## 3. Update Service Layer
- [x] Modify `BeerService` interface to use DTOs:
  - [x] Update `saveBeer` method to accept and return `BeerDto`
  - [x] Update `getBeerById` method to return `Optional<BeerDto>`
  - [x] Update `getAllBeers` method to return `List<BeerDto>`
  - [x] Keep `deleteBeerById` method signature unchanged
- [x] Update `BeerServiceImpl` to:
  - [x] Inject `BeerMapper` as a constructor parameter
  - [x] Update implementation to convert between DTOs and entities
  - [x] Add `@Transactional(readOnly = true)` for query methods
  - [x] Add `@Transactional` for data-modifying methods

## 4. Update Controller Layer
- [x] Update `BeerController` to:
  - [x] Update import statements to use `BeerDto` instead of `Beer`
  - [x] Update method parameters and return types to use `BeerDto`
  - [x] Add `@Valid` annotation for request validation in POST and PUT methods
  - [x] Ensure all endpoints maintain the same REST API behavior and HTTP status codes

## 5. Update Tests
- [x] Update `BeerControllerTest`:
  - [x] Create test helper methods to create `BeerDto` objects
  - [x] Update mock service method expectations to use DTOs
  - [x] Update assertions to match DTO structure
  - [x] Add tests for validation errors
- [x] Update `BeerServiceTest`:
  - [x] Create `BeerDto` objects for input
  - [x] Update mock repository expectations
  - [x] Update assertions to verify DTOs

## 6. Testing and Verification
- [x] Run all tests to ensure they pass
- [x] Verify that the API continues to function as before from a client perspective
- [x] Verify that input validation is properly implemented
- [x] Verify that the web layer is properly decoupled from the persistence layer