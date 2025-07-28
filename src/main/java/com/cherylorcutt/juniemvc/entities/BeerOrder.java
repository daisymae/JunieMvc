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