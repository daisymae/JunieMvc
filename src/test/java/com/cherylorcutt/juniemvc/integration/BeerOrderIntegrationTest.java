package com.cherylorcutt.juniemvc.integration;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.entities.BeerOrder;
import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.CustomerDto;
import com.cherylorcutt.juniemvc.models.commands.BeerOrderLineCommand;
import com.cherylorcutt.juniemvc.models.commands.CreateBeerOrderCommand;
import com.cherylorcutt.juniemvc.repositories.BeerOrderRepository;
import com.cherylorcutt.juniemvc.repositories.BeerRepository;
import com.cherylorcutt.juniemvc.repositories.CustomerRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // Uses application-test.properties with H2 database configuration
class BeerOrderIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private BeerRepository beerRepository;

    @Autowired
    private BeerOrderRepository beerOrderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Customer customer;
    private Beer beer;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        beerOrderRepository.deleteAll();
        customerRepository.deleteAll();
        beerRepository.deleteAll();

        // Create test customer
        customer = Customer.builder()
                .customerName("Integration Test Customer")
                .email("integration@example.com")
                .phone("555-123-4567")
                .build();
        customer = customerRepository.save(customer);

        // Create test beer
        beer = Beer.builder()
                .beerName("Integration Test Beer")
                .beerStyle("IPA")
                .upc("123456789")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(100)
                .build();
        beer = beerRepository.save(beer);
    }

    @Test
    void createBeerOrder_ShouldCreateOrderAndReturnDto() {
        // given
        BeerOrderLineCommand lineCommand = BeerOrderLineCommand.builder()
                .beerId(beer.getId())
                .orderQuantity(10)
                .build();

        CreateBeerOrderCommand createCommand = CreateBeerOrderCommand.builder()
                .customerId(customer.getId())
                .beerOrderLines(Collections.singletonList(lineCommand))
                .build();

        // when
        ResponseEntity<BeerOrderDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/orders",
                createCommand,
                BeerOrderDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        BeerOrderDto orderDto = response.getBody();
        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getCustomerId()).isEqualTo(customer.getId());
        assertThat(orderDto.getOrderStatus()).isEqualTo("NEW");
        assertThat(orderDto.getBeerOrderLines()).hasSize(1);
        assertThat(orderDto.getBeerOrderLines().getFirst().getBeerId()).isEqualTo(beer.getId());
        assertThat(orderDto.getBeerOrderLines().getFirst().getOrderQuantity()).isEqualTo(10);

        // Verify order was saved in database
        List<BeerOrder> savedOrders = beerOrderRepository.findAllByCustomerWithBeerOrderLines(customer);
        assertThat(savedOrders).hasSize(1);
        assertThat(savedOrders.getFirst().getOrderStatus()).isEqualTo(BeerOrder.OrderStatus.NEW);
        assertThat(savedOrders.getFirst().getBeerOrderLines()).hasSize(1);
    }

    @Test
    void getBeerOrderById_ShouldReturnOrder() {
        // given
        // Create an order first
        BeerOrderLineCommand lineCommand = BeerOrderLineCommand.builder()
                .beerId(beer.getId())
                .orderQuantity(5)
                .build();

        CreateBeerOrderCommand createCommand = CreateBeerOrderCommand.builder()
                .customerId(customer.getId())
                .beerOrderLines(Collections.singletonList(lineCommand))
                .build();

        ResponseEntity<BeerOrderDto> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/orders",
                createCommand,
                BeerOrderDto.class);
        
        BeerOrderDto createdOrder = createResponse.getBody();
        assertThat(createdOrder).isNotNull();
        Integer orderId = createdOrder.getId();

        // when
        ResponseEntity<BeerOrderDto> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/orders/" + orderId,
                BeerOrderDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BeerOrderDto orderDto = response.getBody();
        assertThat(orderDto).isNotNull();
        assertThat(orderDto.getId()).isEqualTo(orderId);
        assertThat(orderDto.getCustomerId()).isEqualTo(customer.getId());
        assertThat(orderDto.getOrderStatus()).isEqualTo("NEW");
    }

    @Test
    void cancelBeerOrder_ShouldCancelOrder() {
        // given
        // Create an order first
        BeerOrderLineCommand lineCommand = BeerOrderLineCommand.builder()
                .beerId(beer.getId())
                .orderQuantity(5)
                .build();

        CreateBeerOrderCommand createCommand = CreateBeerOrderCommand.builder()
                .customerId(customer.getId())
                .beerOrderLines(Collections.singletonList(lineCommand))
                .build();

        ResponseEntity<BeerOrderDto> createResponse = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/orders",
                createCommand,
                BeerOrderDto.class);
        
        BeerOrderDto createdOrder = createResponse.getBody();
        assertThat(createdOrder).isNotNull();
        Integer orderId = createdOrder.getId();

        // when
        ResponseEntity<Void> response = restTemplate.exchange(
                "http://localhost:" + port + "/api/v1/orders/" + orderId + "/cancel",
                HttpMethod.PUT,
                HttpEntity.EMPTY,
                Void.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        
        // Verify order status was updated in database
        BeerOrder cancelledOrder = beerOrderRepository.findById(orderId).orElse(null);
        assertThat(cancelledOrder).isNotNull();
        assertThat(cancelledOrder.getOrderStatus()).isEqualTo(BeerOrder.OrderStatus.CANCELLED);
    }

    @Test
    void getBeerOrdersByCustomerId_ShouldReturnCustomerOrders() {
        // given
        // Create two orders for the customer
        BeerOrderLineCommand lineCommand1 = BeerOrderLineCommand.builder()
                .beerId(beer.getId())
                .orderQuantity(5)
                .build();

        CreateBeerOrderCommand createCommand1 = CreateBeerOrderCommand.builder()
                .customerId(customer.getId())
                .beerOrderLines(Collections.singletonList(lineCommand1))
                .build();

        BeerOrderLineCommand lineCommand2 = BeerOrderLineCommand.builder()
                .beerId(beer.getId())
                .orderQuantity(10)
                .build();

        CreateBeerOrderCommand createCommand2 = CreateBeerOrderCommand.builder()
                .customerId(customer.getId())
                .beerOrderLines(Collections.singletonList(lineCommand2))
                .build();

        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/orders",
                createCommand1,
                BeerOrderDto.class);

        restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/orders",
                createCommand2,
                BeerOrderDto.class);

        // when
        ResponseEntity<BeerOrderDto[]> response = restTemplate.getForEntity(
                "http://localhost:" + port + "/api/v1/orders/customer/" + customer.getId(),
                BeerOrderDto[].class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        BeerOrderDto[] orders = response.getBody();
        assertThat(orders).isNotNull();
        assertThat(orders).hasSize(2);
        assertThat(orders[0].getCustomerId()).isEqualTo(customer.getId());
        assertThat(orders[1].getCustomerId()).isEqualTo(customer.getId());
    }

    @Test
    void createCustomer_ShouldCreateCustomerAndReturnDto() {
        // given
        CustomerDto customerDto = CustomerDto.builder()
                .customerName("New Integration Customer")
                .email("new-integration@example.com")
                .phone("555-987-6543")
                .build();

        // when
        ResponseEntity<CustomerDto> response = restTemplate.postForEntity(
                "http://localhost:" + port + "/api/v1/customers",
                customerDto,
                CustomerDto.class);

        // then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        CustomerDto createdCustomer = response.getBody();
        assertThat(createdCustomer).isNotNull();
        assertThat(createdCustomer.getId()).isNotNull();
        assertThat(createdCustomer.getCustomerName()).isEqualTo("New Integration Customer");
        assertThat(createdCustomer.getEmail()).isEqualTo("new-integration@example.com");

        // Verify customer was saved in database
        Customer savedCustomer = customerRepository.findByEmail("new-integration@example.com").orElse(null);
        assertThat(savedCustomer).isNotNull();
        assertThat(savedCustomer.getCustomerName()).isEqualTo("New Integration Customer");
    }
}