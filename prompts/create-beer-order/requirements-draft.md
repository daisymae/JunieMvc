### JPA Entity Implementation Guide with Lombok for Beer Order System

Based on the project structure and the task to implement JPA entities with Lombok for a beer order system, I'll provide detailed instructions for implementing the relationships shown in the ERD.

#### Entity Structure Overview

The ERD appears to represent a beer ordering system with the following entities:
- Beer (already implemented)
- Customer
- BeerOrder
- BeerOrderLine

#### Implementation Instructions

### 1. Customer Entity

```java
package com.cherylorcutt.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private String customerName;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "customer")
    private Set<BeerOrder> beerOrders;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
```

### 2. BeerOrder Entity

```java
package com.cherylorcutt.juniemvc.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class BeerOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private String orderStatus;
    private String orderStatusCallbackUrl;

    @ManyToOne
    private Customer customer;

    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL)
    private Set<BeerOrderLine> beerOrderLines;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
```

### 3. BeerOrderLine Entity

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
public class BeerOrderLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Version
    private Integer version;

    private Integer orderQuantity;

    @ManyToOne
    private BeerOrder beerOrder;

    @ManyToOne
    private Beer beer;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    private LocalDateTime updateDate;
}
```

### 4. Update Beer Entity

The existing Beer entity needs to be updated to include the relationship with BeerOrderLine:

```java
// Add this to the existing Beer class
@OneToMany(mappedBy = "beer")
private Set<BeerOrderLine> beerOrderLines;
```

### Implementation Notes

1. **Bidirectional Relationships**:
    - Customer ↔ BeerOrder: One-to-many bidirectional
    - BeerOrder ↔ BeerOrderLine: One-to-many bidirectional
    - Beer ↔ BeerOrderLine: One-to-many bidirectional

2. **Cascade Operations**:
    - BeerOrder → BeerOrderLine: Use CascadeType.ALL to ensure order lines are persisted, updated, and deleted with their parent order

3. **Fetch Types**:
    - Default fetch types are used (EAGER for @ManyToOne and LAZY for @OneToMany)
    - Consider changing to explicit LAZY loading for all relationships to avoid N+1 query problems

4. **Helper Methods**:
    - Add these helper methods to manage bidirectional relationships properly:

```java
// In Customer class
public void addBeerOrder(BeerOrder beerOrder) {
    if (beerOrders == null) {
        beerOrders = new HashSet<>();
    }
    beerOrders.add(beerOrder);
    beerOrder.setCustomer(this);
}

// In BeerOrder class
public void addBeerOrderLine(BeerOrderLine beerOrderLine) {
    if (beerOrderLines == null) {
        beerOrderLines = new HashSet<>();
    }
    beerOrderLines.add(beerOrderLine);
    beerOrderLine.setBeerOrder(this);
}

// In Beer class
public void addBeerOrderLine(BeerOrderLine beerOrderLine) {
    if (beerOrderLines == null) {
        beerOrderLines = new HashSet<>();
    }
    beerOrderLines.add(beerOrderLine);
    beerOrderLine.setBeer(this);
}
```

5. **Lombok Considerations**:
    - Use `@Builder.Default` for collections if you want them initialized in the builder
    - Be careful with `@ToString` to avoid infinite recursion in bidirectional relationships
    - Consider using `@EqualsAndHashCode(of = "id")` to prevent issues with entity equality

6. **Database Constraints**:
    - Add appropriate `@Column` constraints for fields like email, phone, etc.
    - Consider using `@Enumerated(EnumType.STRING)` for the orderStatus field if it represents an enum

7. **Initialization**:
    - Initialize collections to avoid NullPointerException:

```java
// In each entity with collections
@Builder.Default
private Set<BeerOrder> beerOrders = new HashSet<>();
```

8. **Additional Information**:
    - For each entity create appropriate CRUD operations, repositories, services, DTOs, mappers, controllers and tests.
    - When possible, use interfaces to reduce duplication of code in classes.


Following these instructions will create a properly structured JPA entity model with Lombok that represents the relationships in the ERD while adhering to best practices for JPA entity design.