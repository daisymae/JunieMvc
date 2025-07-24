
# JPA Implementation Guide with Lombok for Beer Order System

Based on the provided ERD, I'll outline detailed instructions for implementing the entities and their relationships using JPA with Lombok.

## Entity Overview

The ERD shows three main entities:
1. **Beer** - Contains beer product information
2. **BeerOrder** - Represents a customer order
3. **BeerOrderLine** - Represents line items in an order (connecting Beer and BeerOrder)

## Implementation Instructions

### 1. Common Base Entity Structure

First, create a base entity class to handle common fields:

```java
@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Version
    private Integer version;
    
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdDate;
    
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
```

### 2. Beer Entity

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Beer extends BaseEntity {
    
    @Column(nullable = false)
    private String beerName;
    
    private String beerStyle;
    
    private String upc;
    
    private Integer quantityOnHand;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal price;
    
    // Bidirectional relationship with BeerOrderLine
    @OneToMany(mappedBy = "beer")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<BeerOrderLine> beerOrderLines = new HashSet<>();
}
```

### 3. BeerOrder Entity

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrder extends BaseEntity {
    
    private String customerRef;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal paymentAmount;
    
    private String status;
    
    // Bidirectional relationship with BeerOrderLine
    @OneToMany(mappedBy = "beerOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<BeerOrderLine> beerOrderLines = new HashSet<>();
    
    // Helper method to manage bidirectional relationship
    public void addBeerOrderLine(BeerOrderLine line) {
        if (beerOrderLines == null) {
            beerOrderLines = new HashSet<>();
        }
        beerOrderLines.add(line);
        line.setBeerOrder(this);
    }
    
    public void removeBeerOrderLine(BeerOrderLine line) {
        beerOrderLines.remove(line);
        line.setBeerOrder(null);
    }
}
```

### 4. BeerOrderLine Entity

```java
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeerOrderLine extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "beer_order_id")
    private BeerOrder beerOrder;
    
    @ManyToOne
    @JoinColumn(name = "beer_id")
    private Beer beer;
    
    private Integer orderQuantity;
    
    private Integer quantityAllocated;
    
    private String status;
}
```

## Implementation Notes

1. **Lombok Annotations**:
    - `@Getter` and `@Setter`: Generate getters and setters
    - `@NoArgsConstructor`: Generate a no-args constructor
    - `@AllArgsConstructor`: Generate a constructor with all fields
    - `@Builder`: Enable the builder pattern
    - `@ToString.Exclude` and `@EqualsAndHashCode.Exclude`: Prevent circular references in toString() and equals()/hashCode()

2. **JPA Annotations**:
    - `@Entity`: Mark class as JPA entity
    - `@MappedSuperclass`: Base class for entities
    - `@Id`: Primary key
    - `@GeneratedValue`: Auto-generate primary key
    - `@Version`: Optimistic locking
    - `@Column`: Column properties
    - `@OneToMany` and `@ManyToOne`: Relationship mappings
    - `@JoinColumn`: Foreign key column
    - `@CreationTimestamp` and `@UpdateTimestamp`: Automatic timestamp management

3. **Bidirectional Relationship Management**:
    - Use helper methods in BeerOrder to maintain both sides of the relationship
    - Use `mappedBy` to indicate the owning side of relationships
    - Use `CascadeType.ALL` and `orphanRemoval = true` for parent-child relationships

4. **Collection Initialization**:
    - Initialize collections to empty sets to avoid null pointer exceptions
    - Use `@Builder.Default` to ensure collections are initialized when using the builder pattern

5. **Data Types**:
    - Use `BigDecimal` for monetary values with appropriate precision and scale
    - Use `LocalDateTime` for date/time fields

This implementation follows JPA best practices and leverages Lombok to reduce boilerplate code while maintaining proper entity relationships as shown in the ERD.