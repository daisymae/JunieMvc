# Task List for Adding DTOs to the Beer API

## 1. Create DTO Classes
- [ ] Create a new package `com.cherylorcutt.juniemvc.models`
- [ ] Create `BeerDto` class with the following:
  - [ ] Add fields: id, version, beerName, beerStyle, upc, quantityOnHand, price
  - [ ] Add Lombok annotations: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
  - [ ] Add Jakarta Validation annotations:
    - [ ] `@NotBlank` for beerName, beerStyle, upc
    - [ ] `@NotNull` for price, quantityOnHand
    - [ ] `@PositiveOrZero` for quantityOnHand
    - [ ] `@Positive` for price

## 2. Create MapStruct Mapper
- [ ] Add MapStruct dependency to pom.xml
- [ ] Create a new package `com.cherylorcutt.juniemvc.mappers`
- [ ] Create `BeerMapper` interface with MapStruct:
  - [ ] Add `@Mapper(componentModel = "spring")` annotation
  - [ ] Add method to convert Beer to BeerDto: `BeerDto beerToBeerDto(Beer beer)`
  - [ ] Add method to convert BeerDto to Beer with mappings:
    - [ ] Add `@Mapping(target = "id", ignore = true)`
    - [ ] Add `@Mapping(target = "createdDate", ignore = true)`
    - [ ] Add `@Mapping(target = "updateDate", ignore = true)`

## 3. Update Service Layer
- [ ] Modify `BeerService` interface to use DTOs:
  - [ ] Update `saveBeer` method to accept and return `BeerDto`
  - [ ] Update `getBeerById` method to return `Optional<BeerDto>`
  - [ ] Update `getAllBeers` method to return `List<BeerDto>`
  - [ ] Keep `deleteBeerById` method signature unchanged
- [ ] Update `BeerServiceImpl` to:
  - [ ] Inject `BeerMapper` as a constructor parameter
  - [ ] Update implementation to convert between DTOs and entities
  - [ ] Add `@Transactional(readOnly = true)` for query methods
  - [ ] Add `@Transactional` for data-modifying methods

## 4. Update Controller Layer
- [ ] Update `BeerController` to:
  - [ ] Update import statements to use `BeerDto` instead of `Beer`
  - [ ] Update method parameters and return types to use `BeerDto`
  - [ ] Add `@Valid` annotation for request validation in POST and PUT methods
  - [ ] Ensure all endpoints maintain the same REST API behavior and HTTP status codes

## 5. Update Tests
- [ ] Update `BeerControllerTest`:
  - [ ] Create test helper methods to create `BeerDto` objects
  - [ ] Update mock service method expectations to use DTOs
  - [ ] Update assertions to match DTO structure
  - [ ] Add tests for validation errors
- [ ] Update `BeerServiceTest`:
  - [ ] Create `BeerDto` objects for input
  - [ ] Update mock repository expectations
  - [ ] Update assertions to verify DTOs

## 6. Testing and Verification
- [ ] Run all tests to ensure they pass
- [ ] Verify that the API continues to function as before from a client perspective
- [ ] Verify that input validation is properly implemented
- [ ] Verify that the web layer is properly decoupled from the persistence layer