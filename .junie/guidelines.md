# JunieMvc Developer Guidelines

## Project Overview

JunieMvc is a Spring Boot application that provides a RESTful API for managing beer data. It follows a standard layered architecture with controllers, services, repositories, and entities.

### Tech Stack

- **Java 21**: Programming language
- **Spring Boot 3.5.3**: Application framework
- **Spring Data JPA**: Data access layer
- **H2 Database**: In-memory database for development and testing
- **Flyway**: Database migration tool
- **Lombok**: Reduces boilerplate code
- **MapStruct**: Object mapping between layers
- **JUnit 5**: Testing framework
- **Mockito**: Mocking framework for testing
- **Maven**: Build and dependency management tool

## Project Structure

The project follows a standard Spring Boot application structure:

```
src/
├── main/
│   ├── java/
│   │   └── com/cherylorcutt/juniemvc/
│   │       ├── controllers/    # REST API endpoints
│   │       ├── entities/       # JPA entity classes
│   │       ├── repositories/   # Spring Data JPA repositories
│   │       ├── services/       # Business logic layer
│   │       └── JunieMvcApplication.java  # Application entry point
│   └── resources/
│       └── application.properties  # Application configuration
└── test/
    ├── java/
    │   └── com/cherylorcutt/juniemvc/
    │       ├── controllers/    # Controller tests
    │       ├── repositories/   # Repository tests
    │       ├── services/       # Service tests
    │       └── JunieMvcApplicationTests.java
    └── resources/
        └── application.properties  # Test configuration
```

### Key Components

- **Entities**: Domain objects mapped to database tables (e.g., `Beer.java`)
- **Repositories**: Data access interfaces extending Spring Data JPA repositories
- **Services**: Business logic interfaces and implementations
- **Controllers**: REST API endpoints that handle HTTP requests

## Build and Run Instructions

### Prerequisites

- Java 21 JDK installed
- Maven installed (or use the included Maven wrapper)

### Building the Project

To build the project, run:

```bash
# Using Maven
mvn clean install

# Using Maven wrapper
./mvnw clean install
```

### Running the Application

To run the application locally:

```bash
# Using Maven
mvn spring-boot:run

# Using Maven wrapper
./mvnw spring-boot:run
```

The application will start on port 8080 by default. You can access the API at `http://localhost:8080/api/v1/beers`.

### Database Access

The application uses an H2 in-memory database. When running in test mode, you can access the H2 console at:

```
http://localhost:8080/h2-console
```

Connection details:
- JDBC URL: `jdbc:h2:mem:testdb`
- Username: `sa`
- Password: `password`

## Testing Guidelines

The project uses JUnit 5 and Mockito for testing. Tests are organized by layer, following the same package structure as the main code.

### Running Tests

To run all tests:

```bash
mvn test
```

To run a specific test class:

```bash
mvn test -Dtest=BeerControllerTest
```

### Test Structure

- **Unit Tests**: Test individual components in isolation
  - **Controller Tests**: Use `@WebMvcTest` to test REST endpoints with MockMvc
  - **Service Tests**: Use `@ExtendWith(MockitoExtension.class)` with `@Mock` and `@InjectMocks`
  - **Repository Tests**: Use `@DataJpaTest` to test JPA repositories with an in-memory database

### Test Naming Convention

Tests follow a clear naming convention:

- `testMethodName_Condition_ExpectedResult`
- Example: `testGetBeerById_Found_ReturnsOk`

### Test Organization

Each test follows the Arrange-Act-Assert pattern (Given-When-Then):

1. **Arrange/Given**: Set up test data and expectations
2. **Act/When**: Execute the method being tested
3. **Assert/Then**: Verify the results

## Best Practices

### Code Organization

- Follow the layered architecture pattern (controller → service → repository)
- Keep business logic in the service layer, not in controllers or repositories
- Use interfaces for services to enable easier testing and future implementation changes
- Use constructor injection for dependencies instead of field injection

### API Design

- Follow RESTful principles for API endpoints
- Use appropriate HTTP methods (GET, POST, PUT, DELETE)
- Return appropriate HTTP status codes (200, 201, 204, 404, etc.)
- Version your APIs (e.g., `/api/v1/beers`)

### Database

- Use JPA entities with appropriate annotations
- Include version field for optimistic locking
- Use audit fields (createdDate, updateDate) for tracking changes
- Define appropriate relationships between entities

### Testing

- Write tests for all layers (controller, service, repository)
- Mock dependencies for unit tests
- Use appropriate test slices (@WebMvcTest, @DataJpaTest) to focus testing
- Follow the Arrange-Act-Assert pattern in tests
- Test both success and failure scenarios