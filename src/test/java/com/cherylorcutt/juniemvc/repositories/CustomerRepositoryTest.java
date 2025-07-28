package com.cherylorcutt.juniemvc.repositories;

import com.cherylorcutt.juniemvc.entities.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findByEmail_ShouldReturnCustomer_WhenEmailExists() {
        // given
        Customer customer = Customer.builder()
                .customerName("Test Customer")
                .email("test@example.com")
                .phone("123-456-7890")
                .build();
        entityManager.persist(customer);
        entityManager.flush();

        // when
        Optional<Customer> found = customerRepository.findByEmail("test@example.com");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getCustomerName()).isEqualTo("Test Customer");
        assertThat(found.get().getEmail()).isEqualTo("test@example.com");
        assertThat(found.get().getPhone()).isEqualTo("123-456-7890");
    }

    @Test
    void findByEmail_ShouldReturnEmpty_WhenEmailDoesNotExist() {
        // when
        Optional<Customer> found = customerRepository.findByEmail("nonexistent@example.com");

        // then
        assertThat(found).isEmpty();
    }

    @Test
    void save_ShouldPersistCustomer() {
        // given
        Customer customer = Customer.builder()
                .customerName("New Customer")
                .email("new@example.com")
                .phone("987-654-3210")
                .build();

        // when
        Customer saved = customerRepository.save(customer);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCustomerName()).isEqualTo("New Customer");
        assertThat(saved.getEmail()).isEqualTo("new@example.com");
        assertThat(saved.getPhone()).isEqualTo("987-654-3210");
        
        // verify it's in the database
        Customer found = entityManager.find(Customer.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getCustomerName()).isEqualTo("New Customer");
    }
}