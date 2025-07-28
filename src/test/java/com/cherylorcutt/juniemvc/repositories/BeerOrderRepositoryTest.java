package com.cherylorcutt.juniemvc.repositories;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.entities.BeerOrder;
import com.cherylorcutt.juniemvc.entities.BeerOrderLine;
import com.cherylorcutt.juniemvc.entities.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BeerOrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    private Customer customer;
    private Beer beer;

    @BeforeEach
    void setUp() {
        // Create and persist a customer
        customer = Customer.builder()
                .customerName("Test Customer")
                .email("test@example.com")
                .phone("123-456-7890")
                .build();
        entityManager.persist(customer);

        // Create and persist a beer
        beer = Beer.builder()
                .beerName("Test Beer")
                .beerStyle("IPA")
                .upc("123456789")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(100)
                .build();
        entityManager.persist(beer);

        entityManager.flush();
    }

    @Test
    void findAllByCustomer_ShouldReturnOrders_WhenCustomerHasOrders() {
        // given
        BeerOrder order1 = BeerOrder.builder()
                .orderStatus(BeerOrder.OrderStatus.NEW)
                .customer(customer)
                .build();
        entityManager.persist(order1);

        BeerOrder order2 = BeerOrder.builder()
                .orderStatus(BeerOrder.OrderStatus.PROCESSING)
                .customer(customer)
                .build();
        entityManager.persist(order2);

        entityManager.flush();

        // when
        List<BeerOrder> found = beerOrderRepository.findAllByCustomer(customer);

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(BeerOrder::getOrderStatus)
                .containsExactlyInAnyOrder(BeerOrder.OrderStatus.NEW, BeerOrder.OrderStatus.PROCESSING);
    }

    @Test
    void findAllByCustomer_ShouldReturnEmptyList_WhenCustomerHasNoOrders() {
        // given
        Customer newCustomer = Customer.builder()
                .customerName("New Customer")
                .email("new@example.com")
                .build();
        entityManager.persist(newCustomer);
        entityManager.flush();

        // when
        List<BeerOrder> found = beerOrderRepository.findAllByCustomer(newCustomer);

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void findAllByOrderStatus_ShouldReturnOrders_WithMatchingStatus() {
        // given
        BeerOrder order1 = BeerOrder.builder()
                .orderStatus(BeerOrder.OrderStatus.NEW)
                .customer(customer)
                .build();
        entityManager.persist(order1);

        BeerOrder order2 = BeerOrder.builder()
                .orderStatus(BeerOrder.OrderStatus.PROCESSING)
                .customer(customer)
                .build();
        entityManager.persist(order2);

        BeerOrder order3 = BeerOrder.builder()
                .orderStatus(BeerOrder.OrderStatus.NEW)
                .customer(customer)
                .build();
        entityManager.persist(order3);

        entityManager.flush();

        // when
        List<BeerOrder> found = beerOrderRepository.findAllByOrderStatus(BeerOrder.OrderStatus.NEW);

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(BeerOrder::getOrderStatus)
                .containsOnly(BeerOrder.OrderStatus.NEW);
    }

    @Test
    void save_ShouldPersistOrderWithLines() {
        // given
        BeerOrder order = BeerOrder.builder()
                .orderStatus(BeerOrder.OrderStatus.NEW)
                .customer(customer)
                .build();

        BeerOrderLine line = BeerOrderLine.builder()
                .orderQuantity(10)
                .beer(beer)
                .build();
        order.addBeerOrderLine(line);

        // when
        BeerOrder saved = beerOrderRepository.save(order);
        entityManager.flush();
        entityManager.clear();

        // then
        BeerOrder found = entityManager.find(BeerOrder.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getOrderStatus()).isEqualTo(BeerOrder.OrderStatus.NEW);
        assertThat(found.getCustomer().getId()).isEqualTo(customer.getId());
        assertThat(found.getBeerOrderLines()).hasSize(1);
        
        BeerOrderLine foundLine = found.getBeerOrderLines().iterator().next();
        assertThat(foundLine.getOrderQuantity()).isEqualTo(10);
        assertThat(foundLine.getBeer().getId()).isEqualTo(beer.getId());
    }
}