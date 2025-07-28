# Beer Order System Implementation Tasks

## 1. Entity Implementation

### 1.1 Update Beer Entity
- [x] Update Beer entity to include relationship with BeerOrderLine
- [x] Add helper method to manage bidirectional relationship

### 1.2 Implement Customer Entity
- [x] Create Customer entity with ID, version, and audit fields
- [x] Add customer details (name, email, phone)
- [x] Implement one-to-many relationship with BeerOrder
- [x] Add helper method for managing bidirectional relationship

### 1.3 Implement BeerOrder Entity
- [x] Create BeerOrder entity with ID, version, and audit fields
- [x] Implement OrderStatus enum (NEW, PENDING, PROCESSING, COMPLETED, CANCELLED, DELIVERY_EXCEPTION)
- [x] Add many-to-one relationship with Customer
- [x] Add one-to-many relationship with BeerOrderLine
- [x] Implement helper method for managing bidirectional relationship

### 1.4 Implement BeerOrderLine Entity
- [x] Create BeerOrderLine entity with ID, version, and audit fields
- [x] Add order quantity field
- [x] Implement many-to-one relationship with BeerOrder
- [x] Implement many-to-one relationship with Beer

## 2. Repository Implementation

### 2.1 Create CustomerRepository
- [x] Create CustomerRepository interface extending JpaRepository
- [x] Add findByEmail method

### 2.2 Create BeerOrderRepository
- [x] Create BeerOrderRepository interface extending JpaRepository
- [x] Add findAllByCustomer method
- [x] Add findAllByOrderStatus method

## 3. DTO and Command Object Implementation

### 3.1 Create CustomerDto
- [x] Create CustomerDto class with ID and version fields
- [x] Add customer details (name, email, phone)
- [x] Add validation annotations for required fields and email format

### 3.2 Create BeerOrderDto
- [x] Create BeerOrderDto class with ID, version, and audit fields
- [x] Add order status and callback URL fields
- [x] Add customer ID field
- [x] Add list of BeerOrderLineDto objects
- [x] Implement validation annotations for required fields

### 3.3 Create BeerOrderLineDto
- [x] Create BeerOrderLineDto class with ID and version fields
- [x] Add order quantity field
- [x] Add beer ID and basic beer information fields
- [x] Implement validation annotations for required fields and positive quantity

### 3.4 Create Command Objects
- [x] Create CreateBeerOrderCommand with customer ID, list of order lines, and callback URL
- [x] Create BeerOrderLineCommand with beer ID and order quantity
- [x] Add validation annotations for required fields and constraints

## 4. Mapper Implementation

### 4.1 Create CustomerMapper
- [x] Create CustomerMapper interface using MapStruct
- [x] Add customerToCustomerDto method
- [x] Add customerDtoToCustomer method with appropriate field ignores

### 4.2 Create BeerOrderMapper
- [x] Create BeerOrderMapper interface using MapStruct
- [x] Add beerOrderToBeerOrderDto method with customer ID mapping
- [x] Add beerOrderDtoToBeerOrder method with appropriate field ignores
- [x] Add createBeerOrderCommandToBeerOrder method with appropriate field ignores and default values

### 4.3 Create BeerOrderLineMapper
- [x] Create BeerOrderLineMapper interface using MapStruct
- [x] Add beerOrderLineToBeerOrderLineDto method with beer information mapping
- [x] Add beerOrderLineDtoToBeerOrderLine method with appropriate field ignores
- [x] Add beerOrderLineCommandToBeerOrderLine method with appropriate field ignores

## 5. Service Implementation

### 5.1 Create CustomerService Interface
- [x] Create CustomerService interface
- [x] Add saveCustomer method
- [x] Add getCustomerById method
- [x] Add getCustomerByEmail method
- [x] Add getAllCustomers method
- [x] Add deleteCustomerById method

### 5.2 Create CustomerServiceImpl
- [x] Create CustomerServiceImpl class implementing CustomerService
- [x] Implement constructor injection for dependencies
- [x] Add transaction management with @Transactional annotations
- [x] Implement all interface methods
- [x] Add proper error handling

### 5.3 Create BeerOrderService Interface
- [x] Create BeerOrderService interface
- [x] Add createBeerOrder method
- [x] Add getBeerOrderById method
- [x] Add getAllBeerOrders method
- [x] Add getBeerOrdersByCustomerId method
- [x] Add cancelBeerOrder method

### 5.4 Create BeerOrderServiceImpl
- [x] Create BeerOrderServiceImpl class implementing BeerOrderService
- [x] Implement constructor injection for dependencies
- [x] Add transaction management with @Transactional annotations
- [x] Implement all interface methods
- [x] Add proper error handling and business logic for order status transitions

## 6. Controller Implementation

### 6.1 Create CustomerController
- [x] Create CustomerController class
- [x] Implement constructor injection for CustomerService
- [x] Add request mapping to "/api/v1/customers"
- [x] Implement methods for CRUD operations with appropriate HTTP methods and status codes
- [x] Add validation of request bodies
- [x] Implement proper error handling

### 6.2 Create BeerOrderController
- [x] Create BeerOrderController class
- [x] Implement constructor injection for BeerOrderService
- [x] Add request mapping to "/api/v1/orders"
- [x] Implement methods for creating orders, retrieving orders, and cancelling orders
- [x] Add validation of request bodies
- [x] Implement proper error handling

## 7. Exception Handling Implementation
- [x] Update GlobalExceptionHandler to handle CustomerNotFoundException
- [x] Update GlobalExceptionHandler to handle BeerNotFoundException
- [x] Update GlobalExceptionHandler to handle OrderNotFoundException
- [x] Update GlobalExceptionHandler to handle InvalidOrderStateException

## 8. Testing Implementation

### 8.1 Unit Tests
- [x] Create repository tests using @DataJpaTest
- [x] Create service tests with mocked dependencies
- [x] Create controller tests with MockMvc

### 8.2 Integration Tests
- [x] Create integration tests using Testcontainers
- [x] Implement end-to-end tests for order creation and management
- [x] Add tests for edge cases and error scenarios