# Beer Order System Implementation Plan

## 1. Overview

This document outlines the implementation plan for extending the existing beer management application with order management capabilities. The plan is based on the requirements specified in `requirements.md` and follows the established patterns and best practices in the current codebase.

## 2. Current Project Structure Analysis

The current project follows a well-structured layered architecture:

- **Entity Layer**: JPA entities with Lombok annotations (e.g., `Beer.java`)
- **Repository Layer**: Spring Data JPA repositories (e.g., `BeerRepository.java`)
- **Service Layer**: Service interfaces and implementations with transaction management (e.g., `BeerService.java`, `BeerServiceImpl.java`)
- **Controller Layer**: REST controllers with proper request mapping and status codes (e.g., `BeerController.java`)
- **DTO Layer**: Data Transfer Objects with validation annotations (e.g., `BeerDto.java`)
- **Mapper Layer**: MapStruct mappers for entity-DTO conversion (e.g., `BeerMapper.java`)
- **Exception Handling**: Centralized exception handling with `GlobalExceptionHandler.java`

## 3. Implementation Plan

### 3.1 Entity Implementation

#### 3.1.1 Update Beer Entity

Update the existing `Beer` entity to include a relationship with `BeerOrderLine`:

```java
@OneToMany(mappedBy = "beer", fetch = FetchType.LAZY)
@ToString.Exclude
@Builder.Default
private Set<BeerOrderLine> beerOrderLines = new HashSet<>();

// Helper method to manage bidirectional relationship
public void addBeerOrderLine(BeerOrderLine beerOrderLine) {
    beerOrderLines.add(beerOrderLine);
    beerOrderLine.setBeer(this);
}
```

#### 3.1.2 Implement Customer Entity

Create a new `Customer` entity with the following features:
- ID, version, and audit fields (createdDate, updateDate)
- Customer details (name, email, phone)
- One-to-many relationship with BeerOrder
- Helper method for managing bidirectional relationship

#### 3.1.3 Implement BeerOrder Entity

Create a new `BeerOrder` entity with the following features:
- ID, version, and audit fields (createdDate, updateDate)
- OrderStatus enum (NEW, PENDING, PROCESSING, COMPLETED, CANCELLED, DELIVERY_EXCEPTION)
- Many-to-one relationship with Customer
- One-to-many relationship with BeerOrderLine
- Helper method for managing bidirectional relationship

#### 3.1.4 Implement BeerOrderLine Entity

Create a new `BeerOrderLine` entity with the following features:
- ID, version, and audit fields (createdDate, updateDate)
- Order quantity
- Many-to-one relationship with BeerOrder
- Many-to-one relationship with Beer

### 3.2 Repository Implementation

#### 3.2.1 Create CustomerRepository

Create a new `CustomerRepository` interface extending `JpaRepository<Customer, Integer>` with the following methods:
- `Optional<Customer> findByEmail(String email)`

#### 3.2.2 Create BeerOrderRepository

Create a new `BeerOrderRepository` interface extending `JpaRepository<BeerOrder, Integer>` with the following methods:
- `List<BeerOrder> findAllByCustomer(Customer customer)`
- `List<BeerOrder> findAllByOrderStatus(BeerOrder.OrderStatus orderStatus)`

### 3.3 DTO and Command Object Implementation

#### 3.3.1 Create CustomerDto

Create a new `CustomerDto` class with the following features:
- ID and version fields
- Customer details (name, email, phone)
- Validation annotations for required fields and email format

#### 3.3.2 Create BeerOrderDto

Create a new `BeerOrderDto` class with the following features:
- ID, version, and audit fields (createdDate, updateDate)
- Order status and callback URL
- Customer ID
- List of BeerOrderLineDto objects
- Validation annotations for required fields

#### 3.3.3 Create BeerOrderLineDto

Create a new `BeerOrderLineDto` class with the following features:
- ID and version fields
- Order quantity
- Beer ID and basic beer information (name, style, UPC)
- Validation annotations for required fields and positive quantity

#### 3.3.4 Create Command Objects

Create command objects for creating beer orders:
- `CreateBeerOrderCommand` with customer ID, list of order lines, and callback URL
- `BeerOrderLineCommand` with beer ID and order quantity
- Validation annotations for required fields and constraints

### 3.4 Mapper Implementation

#### 3.4.1 Create CustomerMapper

Create a new `CustomerMapper` interface using MapStruct with the following methods:
- `CustomerDto customerToCustomerDto(Customer customer)`
- `Customer customerDtoToCustomer(CustomerDto customerDto)` with appropriate field ignores

#### 3.4.2 Create BeerOrderMapper

Create a new `BeerOrderMapper` interface using MapStruct with the following methods:
- `BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder)` with customer ID mapping
- `BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto beerOrderDto, Customer customer)` with appropriate field ignores
- `BeerOrder createBeerOrderCommandToBeerOrder(CreateBeerOrderCommand command, Customer customer)` with appropriate field ignores and default values

#### 3.4.3 Create BeerOrderLineMapper

Create a new `BeerOrderLineMapper` interface using MapStruct with the following methods:
- `BeerOrderLineDto beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine)` with beer information mapping
- `BeerOrderLine beerOrderLineDtoToBeerOrderLine(BeerOrderLineDto beerOrderLineDto, Beer beer)` with appropriate field ignores
- `BeerOrderLine beerOrderLineCommandToBeerOrderLine(BeerOrderLineCommand command, Beer beer)` with appropriate field ignores

### 3.5 Service Implementation

#### 3.5.1 Create CustomerService Interface

Create a new `CustomerService` interface with the following methods:
- `CustomerDto saveCustomer(CustomerDto customerDto)`
- `Optional<CustomerDto> getCustomerById(Integer id)`
- `Optional<CustomerDto> getCustomerByEmail(String email)`
- `List<CustomerDto> getAllCustomers()`
- `boolean deleteCustomerById(Integer id)`

#### 3.5.2 Create CustomerServiceImpl

Create a new `CustomerServiceImpl` class implementing `CustomerService` with the following features:
- Constructor injection for dependencies (CustomerRepository, CustomerMapper)
- Transaction management with @Transactional annotations
- Implementation of all interface methods
- Proper error handling

#### 3.5.3 Create BeerOrderService Interface

Create a new `BeerOrderService` interface with the following methods:
- `BeerOrderDto createBeerOrder(CreateBeerOrderCommand command)`
- `Optional<BeerOrderDto> getBeerOrderById(Integer id)`
- `List<BeerOrderDto> getAllBeerOrders()`
- `List<BeerOrderDto> getBeerOrdersByCustomerId(Integer customerId)`
- `boolean cancelBeerOrder(Integer id)`

#### 3.5.4 Create BeerOrderServiceImpl

Create a new `BeerOrderServiceImpl` class implementing `BeerOrderService` with the following features:
- Constructor injection for dependencies (BeerOrderRepository, CustomerRepository, BeerRepository, BeerOrderMapper)
- Transaction management with @Transactional annotations
- Implementation of all interface methods
- Proper error handling and business logic for order status transitions

### 3.6 Controller Implementation

#### 3.6.1 Create CustomerController

Create a new `CustomerController` class with the following features:
- Constructor injection for CustomerService
- Request mapping to "/api/v1/customers"
- Methods for CRUD operations with appropriate HTTP methods and status codes
- Validation of request bodies
- Proper error handling

#### 3.6.2 Create BeerOrderController

Create a new `BeerOrderController` class with the following features:
- Constructor injection for BeerOrderService
- Request mapping to "/api/v1/orders"
- Methods for creating orders, retrieving orders, and cancelling orders
- Validation of request bodies
- Proper error handling

### 3.7 Exception Handling Implementation

Update the `GlobalExceptionHandler` class to handle order-related exceptions:
- `CustomerNotFoundException`
- `BeerNotFoundException`
- `OrderNotFoundException`
- `InvalidOrderStateException`

### 3.8 Testing Strategy

#### 3.8.1 Unit Tests

Create unit tests for each layer:
- Repository tests using @DataJpaTest
- Service tests with mocked dependencies
- Controller tests with MockMvc

#### 3.8.2 Integration Tests

Create integration tests using Testcontainers:
- End-to-end tests for order creation and management
- Tests for edge cases and error scenarios

## 4. Implementation Sequence

The implementation should follow this sequence to respect dependencies:

1. Entity implementation
2. Repository implementation
3. DTO and Command Object implementation
4. Mapper implementation
5. Service implementation
6. Controller implementation
7. Exception handling implementation
8. Testing

## 5. Potential Challenges and Solutions

### 5.1 Database Migration

**Challenge**: Adding new tables to the existing database schema.

**Solution**: Use Flyway or Liquibase for database migrations, or rely on Hibernate's schema generation for development.

### 5.2 Transaction Management

**Challenge**: Ensuring proper transaction boundaries for complex operations.

**Solution**: Use @Transactional annotations at the service layer with appropriate propagation and isolation levels.

### 5.3 Bidirectional Relationships

**Challenge**: Properly managing bidirectional relationships to avoid infinite recursion and ensure data consistency.

**Solution**: Use helper methods in entities to manage both sides of relationships, and use @ToString.Exclude and @EqualsAndHashCode.Of annotations to avoid infinite recursion.

## 6. Best Practices

The implementation should follow these best practices:

### 6.1 Constructor Injection

Use constructor injection for all dependencies to ensure proper initialization and testability.

```java
private final BeerOrderRepository beerOrderRepository;
private final CustomerRepository customerRepository;

public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository,
                          CustomerRepository customerRepository) {
    this.beerOrderRepository = beerOrderRepository;
    this.customerRepository = customerRepository;
}
```

### 6.2 Package-Private Visibility

Use package-private visibility for controllers, their methods, and configuration classes when possible to enforce encapsulation.

```java
@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {
    // Implementation
}
```

### 6.3 Transaction Boundaries

Define clear transaction boundaries in service methods with appropriate read-only settings.

```java
@Override
@Transactional
public BeerOrderDto createBeerOrder(CreateBeerOrderCommand command) {
    // Implementation
}

@Override
@Transactional(readOnly = true)
public Optional<BeerOrderDto> getBeerOrderById(Integer id) {
    // Implementation
}
```

### 6.4 Separation of Web and Persistence Layers

Use DTOs for all controller methods and never expose entities directly.

### 6.5 REST API Design

Follow REST API design principles with versioned endpoints, consistent patterns, and explicit HTTP status codes.

### 6.6 Exception Handling

Implement centralized exception handling with specific exception types and clear error messages.

### 6.7 Logging

Use SLF4J for logging with appropriate log levels and guard expensive log calls.

```java
private static final Logger log = LoggerFactory.getLogger(BeerOrderServiceImpl.class);

@Override
@Transactional
public BeerOrderDto createBeerOrder(CreateBeerOrderCommand command) {
    log.debug("Creating beer order for customer ID: {}", command.getCustomerId());
    // Implementation
}
```

## 7. Conclusion

This implementation plan provides a comprehensive roadmap for extending the existing beer management application with order management capabilities. By following the established patterns and best practices, the new functionality will integrate seamlessly with the existing codebase and provide a robust and maintainable solution.