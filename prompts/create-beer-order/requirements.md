# Beer Order System Implementation Requirements

## 1. Business Context and Overview

The Beer Order System is an extension to our existing beer management application. It will allow customers to place orders for beers, track their order status, and manage their order history. The system will support the following core business capabilities:

- Customer management (create, read, update, delete)
- Order placement and management
- Order line item management (adding beers to orders with quantities)
- Order status tracking

## 2. Data Model

The system will extend the existing data model with the following entities:

- **Customer**: Represents a customer who can place beer orders
- **BeerOrder**: Represents an order placed by a customer
- **BeerOrderLine**: Represents a line item in an order (a specific beer and quantity)

The existing **Beer** entity will be related to the new entities through the BeerOrderLine entity.

### Entity Relationships

- Customer ↔ BeerOrder: One-to-many bidirectional
- BeerOrder ↔ BeerOrderLine: One-to-many bidirectional
- Beer ↔ BeerOrderLine: One-to-many bidirectional

## 3. Technical Requirements

### 3.1 JPA Entity Implementation

#### Customer Entity

```java
package com.cherylorcutt.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = "beerOrders")
@EqualsAndHashCode(of = "id")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @Column(nullable = false)
    private String customerName;
    
    @Column(unique = true)
    private String email;
    
    private String phone;

    @OneToMany(mappedBy = "customer", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<BeerOrder> beerOrders = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
    
    // Helper method to manage bidirectional relationship
    public void addBeerOrder(BeerOrder beerOrder) {
        beerOrders.add(beerOrder);
        beerOrder.setCustomer(this);
    }
}
```

#### BeerOrder Entity

```java
package com.cherylorcutt.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = {"customer", "beerOrderLines"})
@EqualsAndHashCode(of = "id")
public class BeerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    
    private String orderStatusCallbackUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Builder.Default
    private Set<BeerOrderLine> beerOrderLines = new HashSet<>();

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
    
    // Helper method to manage bidirectional relationship
    public void addBeerOrderLine(BeerOrderLine beerOrderLine) {
        beerOrderLines.add(beerOrderLine);
        beerOrderLine.setBeerOrder(this);
    }
    
    // Order status enum
    public enum OrderStatus {
        NEW, PENDING, PROCESSING, COMPLETED, CANCELLED, DELIVERY_EXCEPTION
    }
}
```

#### BeerOrderLine Entity

```java
package com.cherylorcutt.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = {"beerOrder", "beer"})
@EqualsAndHashCode(of = "id")
public class BeerOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    @Column(nullable = false)
    private Integer orderQuantity;

    @ManyToOne(fetch = FetchType.LAZY)
    private BeerOrder beerOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    private Beer beer;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
```

#### Update to Beer Entity

```java
// Add this to the existing Beer class
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

### 3.2 Data Transfer Objects (DTOs)

Following the principle of separating the web layer from the persistence layer, create DTOs for each entity:

#### CustomerDto

```java
package com.cherylorcutt.juniemvc.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for Customer operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Integer id;
    private Integer version;
    
    @NotBlank(message = "Customer name is required")
    private String customerName;
    
    @Email(message = "Please provide a valid email address")
    private String email;
    
    private String phone;
}
```

#### BeerOrderDto

```java
package com.cherylorcutt.juniemvc.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object for BeerOrder operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderDto {
    private Integer id;
    private Integer version;
    private String orderStatus;
    private String orderStatusCallbackUrl;
    
    @NotNull(message = "Customer ID is required")
    private Integer customerId;
    
    @Valid
    private List<BeerOrderLineDto> beerOrderLines;
    
    private LocalDateTime createdDate;
    private LocalDateTime updateDate;
}
```

#### BeerOrderLineDto

```java
package com.cherylorcutt.juniemvc.models;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Transfer Object for BeerOrderLine operations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderLineDto {
    private Integer id;
    private Integer version;
    
    @NotNull(message = "Order quantity is required")
    @Positive(message = "Order quantity must be positive")
    private Integer orderQuantity;
    
    @NotNull(message = "Beer ID is required")
    private Integer beerId;
    
    private String beerName;
    private String beerStyle;
    private String upc;
}
```

### 3.3 Command Objects

For creating and updating orders, use command objects to clearly communicate what input data is expected:

```java
package com.cherylorcutt.juniemvc.models.commands;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Command object for creating a beer order
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBeerOrderCommand {
    
    @NotNull(message = "Customer ID is required")
    private Integer customerId;
    
    @NotEmpty(message = "Order must contain at least one beer")
    @Valid
    private List<BeerOrderLineCommand> beerOrderLines;
    
    private String orderStatusCallbackUrl;
}

/**
 * Command object for beer order line items
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderLineCommand {
    
    @NotNull(message = "Beer ID is required")
    private Integer beerId;
    
    @NotNull(message = "Order quantity is required")
    @Positive(message = "Order quantity must be positive")
    private Integer orderQuantity;
}
```

### 3.4 Mappers

Create mappers using MapStruct to convert between entities and DTOs:

```java
package com.cherylorcutt.juniemvc.mappers;

import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.models.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    CustomerDto customerToCustomerDto(Customer customer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "beerOrders", ignore = true)
    Customer customerDtoToCustomer(CustomerDto customerDto);
}
```

```java
package com.cherylorcutt.juniemvc.mappers;

import com.cherylorcutt.juniemvc.entities.BeerOrder;
import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.commands.CreateBeerOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BeerOrderLineMapper.class, DateMapper.class})
public interface BeerOrderMapper {
    
    @Mapping(target = "customerId", source = "customer.id")
    BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "customer", source = "customer")
    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto beerOrderDto, Customer customer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "orderStatus", constant = "NEW")
    @Mapping(target = "customer", source = "customer")
    BeerOrder createBeerOrderCommandToBeerOrder(CreateBeerOrderCommand command, Customer customer);
}
```

### 3.5 Repositories

Create repositories for the new entities:

```java
package com.cherylorcutt.juniemvc.repositories;

import com.cherylorcutt.juniemvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Optional<Customer> findByEmail(String email);
}
```

```java
package com.cherylorcutt.juniemvc.repositories;

import com.cherylorcutt.juniemvc.entities.BeerOrder;
import com.cherylorcutt.juniemvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerOrderRepository extends JpaRepository<BeerOrder, Integer> {
    List<BeerOrder> findAllByCustomer(Customer customer);
    List<BeerOrder> findAllByOrderStatus(BeerOrder.OrderStatus orderStatus);
}
```

### 3.6 Services

Create service interfaces and implementations for the new entities:

```java
package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.models.CustomerDto;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    CustomerDto saveCustomer(CustomerDto customerDto);
    Optional<CustomerDto> getCustomerById(Integer id);
    Optional<CustomerDto> getCustomerByEmail(String email);
    List<CustomerDto> getAllCustomers();
    boolean deleteCustomerById(Integer id);
}
```

```java
package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.commands.CreateBeerOrderCommand;

import java.util.List;
import java.util.Optional;

public interface BeerOrderService {
    BeerOrderDto createBeerOrder(CreateBeerOrderCommand command);
    Optional<BeerOrderDto> getBeerOrderById(Integer id);
    List<BeerOrderDto> getAllBeerOrders();
    List<BeerOrderDto> getBeerOrdersByCustomerId(Integer customerId);
    boolean cancelBeerOrder(Integer id);
}
```

### 3.7 Controllers

Create REST controllers for the new entities:

```java
package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.models.CustomerDto;
import com.cherylorcutt.juniemvc.services.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customers")
class CustomerController {

    private final CustomerService customerService;

    CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    CustomerDto createCustomer(@Valid @RequestBody CustomerDto customerDto) {
        return customerService.saveCustomer(customerDto);
    }

    @GetMapping("/{id}")
    ResponseEntity<CustomerDto> getCustomerById(@PathVariable Integer id) {
        Optional<CustomerDto> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    List<CustomerDto> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    ResponseEntity<CustomerDto> updateCustomer(@PathVariable Integer id, @Valid @RequestBody CustomerDto customerDto) {
        Optional<CustomerDto> existingCustomer = customerService.getCustomerById(id);
        if (existingCustomer.isPresent()) {
            customerDto.setId(id);
            CustomerDto updatedCustomer = customerService.saveCustomer(customerDto);
            return ResponseEntity.ok(updatedCustomer);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteCustomer(@PathVariable Integer id) {
        boolean deleted = customerService.deleteCustomerById(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

```java
package com.cherylorcutt.juniemvc.controllers;

import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.commands.CreateBeerOrderCommand;
import com.cherylorcutt.juniemvc.services.BeerOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/orders")
class BeerOrderController {

    private final BeerOrderService beerOrderService;

    BeerOrderController(BeerOrderService beerOrderService) {
        this.beerOrderService = beerOrderService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    BeerOrderDto createBeerOrder(@Valid @RequestBody CreateBeerOrderCommand command) {
        return beerOrderService.createBeerOrder(command);
    }

    @GetMapping("/{id}")
    ResponseEntity<BeerOrderDto> getBeerOrderById(@PathVariable Integer id) {
        Optional<BeerOrderDto> beerOrder = beerOrderService.getBeerOrderById(id);
        return beerOrder.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    List<BeerOrderDto> getAllBeerOrders() {
        return beerOrderService.getAllBeerOrders();
    }

    @GetMapping("/customer/{customerId}")
    List<BeerOrderDto> getBeerOrdersByCustomerId(@PathVariable Integer customerId) {
        return beerOrderService.getBeerOrdersByCustomerId(customerId);
    }

    @PutMapping("/{id}/cancel")
    ResponseEntity<Void> cancelBeerOrder(@PathVariable Integer id) {
        boolean cancelled = beerOrderService.cancelBeerOrder(id);
        if (cancelled) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
```

## 4. Implementation Guidelines

### 4.1 Follow Spring Boot Best Practices

1. **Constructor Injection**: Use constructor injection for all dependencies.
   ```java
   private final BeerOrderRepository beerOrderRepository;
   private final CustomerRepository customerRepository;
   private final BeerRepository beerRepository;
   private final BeerOrderMapper beerOrderMapper;

   public BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository,
                              CustomerRepository customerRepository,
                              BeerRepository beerRepository,
                              BeerOrderMapper beerOrderMapper) {
       this.beerOrderRepository = beerOrderRepository;
       this.customerRepository = customerRepository;
       this.beerRepository = beerRepository;
       this.beerOrderMapper = beerOrderMapper;
   }
   ```

2. **Package-private Visibility**: Use package-private visibility for controllers, their methods, and configuration classes when possible.

3. **Transaction Boundaries**: Define clear transaction boundaries in service methods.
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

4. **Disable Open Session in View**: Set `spring.jpa.open-in-view=false` in application.properties.

5. **Separate Web Layer from Persistence Layer**: Use DTOs for all controller methods and never expose entities directly.

6. **REST API Design**: Follow REST API design principles with versioned endpoints, consistent patterns, and explicit HTTP status codes.

7. **Exception Handling**: Implement centralized exception handling.
   ```java
   @RestControllerAdvice
   public class GlobalExceptionHandler {
       
       @ExceptionHandler(CustomerNotFoundException.class)
       public ResponseEntity<ProblemDetail> handleCustomerNotFoundException(CustomerNotFoundException ex) {
           ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                   HttpStatus.NOT_FOUND, ex.getMessage());
           problemDetail.setTitle("Customer Not Found");
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
       }
       
       // Other exception handlers
   }
   ```

### 4.2 Testing Guidelines

1. **Use Testcontainers**: Use Testcontainers for integration tests to mirror production environments.
   ```java
   @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
   @Testcontainers
   class BeerOrderIntegrationTest {
       
       @Container
       static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14.5")
               .withDatabaseName("testdb")
               .withUsername("test")
               .withPassword("test");
       
       @DynamicPropertySource
       static void postgresProperties(DynamicPropertyRegistry registry) {
           registry.add("spring.datasource.url", postgres::getJdbcUrl);
           registry.add("spring.datasource.username", postgres::getUsername);
           registry.add("spring.datasource.password", postgres::getPassword);
       }
       
       // Test methods
   }
   ```

2. **Use Random Port**: Start the application on a random available port for integration tests.

3. **Test Each Layer**: Write unit tests for repositories, services, and controllers.

### 4.3 Logging Guidelines

1. **Use SLF4J**: Use SLF4J for logging, not System.out.println().
   ```java
   private static final Logger log = LoggerFactory.getLogger(BeerOrderServiceImpl.class);
   
   @Override
   @Transactional
   public BeerOrderDto createBeerOrder(CreateBeerOrderCommand command) {
       log.debug("Creating beer order for customer ID: {}", command.getCustomerId());
       // Implementation
   }
   ```

2. **Protect Sensitive Data**: Ensure no credentials or personal information appears in logs.

3. **Guard Expensive Log Calls**: Use level checks for expensive logging operations.
   ```java
   if (log.isDebugEnabled()) {
       log.debug("Order details: {}", generateDetailedOrderInfo(order));
   }
   ```

## 5. Implementation Sequence

1. Implement the JPA entities
2. Create the repositories
3. Implement the DTOs and command objects
4. Create the mappers
5. Implement the service interfaces and implementations
6. Create the controllers
7. Implement exception handling
8. Write tests for each layer
9. Configure logging and monitoring

By following these requirements and guidelines, you will create a well-structured, maintainable, and testable beer order system that integrates seamlessly with the existing beer management functionality.