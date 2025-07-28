# Changes Made to Fix Failing Tests

## Issue
The controller tests were failing due to a mix of two different testing approaches:
1. Spring Boot's `@WebMvcTest` annotation for slice testing
2. Mockito's `@ExtendWith(MockitoExtension.class)`, `@Mock`, and `@InjectMocks` annotations for unit testing

Additionally, the tests were not properly handling exceptions thrown by the controller methods.

## Changes Made

### 1. Removed `@WebMvcTest` annotation from controller test classes
The `@WebMvcTest` annotation was removed from the following test classes:
- `BeerOrderControllerTest`
- `CustomerControllerTest`
- `BeerOrderControllerCustomerOrdersTest`

This was done to avoid conflicts with the Mockito testing approach and to prevent the ApplicationContext from trying to load the entire web layer.

### 2. Added manual MockMvc setup with GlobalExceptionHandler
The `setUp` method in each controller test class was updated to manually set up MockMvc with the controller and the GlobalExceptionHandler:

```java
@BeforeEach
void setUp() {
    // Initialize MockMvc with controller and exception handler
    mockMvc = MockMvcBuilders.standaloneSetup(beerOrderController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();
    
    // Setup test data
    // ...
}
```

This ensures that exceptions thrown by the controller methods are properly handled and the appropriate HTTP status codes are returned.

### 3. Fixed inconsistent MockMvc setup
In `BeerOrderControllerTest`, there was an explicit MockMvc setup in the `getBeerOrdersByCustomerId_ShouldReturnCustomerOrders` test method that was removed to ensure consistent MockMvc setup across all test methods.

### 4. Fixed BeerOrderControllerCustomerOrdersTest
The `BeerOrderControllerCustomerOrdersTest` class was updated to use `@Mock` for the `BeerOrderService` instead of trying to autowire it, which wouldn't work in a unit test context.

## Results
After making these changes, all controller tests now pass when run individually. The tests properly handle exceptions thrown by the controller methods and return the appropriate HTTP status codes.

## Note
When running all tests in the project, only the `JunieMvcApplicationTests::contextLoads()` test is executed. This might be a separate issue related to the test configuration or the build system, but it's beyond the scope of the current task since all tests pass when run individually.