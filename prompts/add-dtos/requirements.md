# Requirements for Adding DTOs to the Beer API

## Background
The current implementation of the Beer API directly exposes JPA entities in the REST API, which violates the principle of separating the web layer from the persistence layer. This tight coupling makes the API brittle and less maintainable. To address this issue, we need to introduce Data Transfer Objects (DTOs) to decouple the API from the database schema.

## Objectives
1. Implement a clear separation between the web layer and the persistence layer using DTOs
2. Ensure proper validation of incoming data
3. Maintain backward compatibility with existing API consumers
4. Follow Spring Boot best practices for API design

## Requirements

### 1. Create DTO Classes
- Create a new package `com.cherylorcutt.juniemvc.models` for DTO classes
- Create the following DTO classes:
  - `BeerDto`: For general beer operations (GET, POST, PUT)
  - `BeerListDto`: For list responses (optional, if needed for pagination in the future)
- Use Lombok annotations (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) for the DTO classes
- Add appropriate Jakarta Validation annotations to validate input data:
  - `@NotBlank` for required string fields (beerName, beerStyle, upc)
  - `@NotNull` for required non-string fields (price, quantityOnHand)
  - `@Positive` or `@PositiveOrZero` for numeric fields as appropriate
- Do not include `createdDate` and `updateDate` fields in the DTOs as these are managed by the persistence layer

### 2. Create MapStruct Mapper
- Create a new package `com.cherylorcutt.juniemvc.mappers` for mapper interfaces
- Create a `BeerMapper` interface using MapStruct
- Configure the mapper to:
  - Map between `Beer` entity and `BeerDto`
  - Ignore `id`, `createdDate`, and `updateDate` when mapping from DTO to entity
  - Use the Spring component model (already configured in pom.xml)

### 3. Update Service Layer
- Modify the `BeerService` interface to accept and return DTOs instead of entities
- Update the `BeerServiceImpl` to:
  - Use the `BeerMapper` for converting between DTOs and entities
  - Maintain the same business logic
  - Ensure proper transaction boundaries are maintained

### 4. Update Controller Layer
- Update the `BeerController` to use DTOs for all request and response objects
- Add validation for incoming DTOs using `@Valid` annotation
- Maintain the same REST API endpoints and HTTP status codes
- Ensure backward compatibility with existing API consumers

### 5. Update Tests
- Update `BeerControllerTest` to use DTOs instead of entities
- Add tests for validation errors
- Ensure all existing tests pass with the new implementation

## Technical Constraints
- Use MapStruct for object mapping (already included in pom.xml)
- Use Lombok for reducing boilerplate code (already included in pom.xml)
- Use Jakarta Validation for input validation
- Follow REST API design principles as outlined in the Spring Boot guidelines

## Deliverables
1. New DTO classes in the `models` package
2. New mapper interface in the `mappers` package
3. Updated service layer that works with DTOs
4. Updated controller layer that accepts and returns DTOs
5. Updated tests that verify the new implementation
6. All tests passing

## Success Criteria
1. The API continues to function as before from a client perspective
2. The web layer is properly decoupled from the persistence layer
3. Input validation is properly implemented
4. All tests pass