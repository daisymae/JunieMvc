package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.entities.BeerOrder;
import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.exceptions.BeerNotFoundException;
import com.cherylorcutt.juniemvc.exceptions.CustomerNotFoundException;
import com.cherylorcutt.juniemvc.exceptions.InvalidOrderStateException;
import com.cherylorcutt.juniemvc.exceptions.OrderNotFoundException;
import com.cherylorcutt.juniemvc.mappers.BeerOrderLineMapper;
import com.cherylorcutt.juniemvc.mappers.BeerOrderMapper;
import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.commands.BeerOrderLineCommand;
import com.cherylorcutt.juniemvc.models.commands.CreateBeerOrderCommand;
import com.cherylorcutt.juniemvc.repositories.BeerOrderRepository;
import com.cherylorcutt.juniemvc.repositories.BeerRepository;
import com.cherylorcutt.juniemvc.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BeerOrderServiceTest {

    @Mock
    private BeerOrderRepository beerOrderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BeerRepository beerRepository;

    @Mock
    private BeerOrderMapper beerOrderMapper;

    @Mock
    private BeerOrderLineMapper beerOrderLineMapper;

    @InjectMocks
    private BeerOrderServiceImpl beerOrderService;

    private Customer customer;
    private Beer beer;
    private BeerOrder beerOrder;
    private BeerOrderDto beerOrderDto;
    private CreateBeerOrderCommand createCommand;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id(1)
                .customerName("Test Customer")
                .email("test@example.com")
                .build();

        beer = Beer.builder()
                .id(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .upc("123456789")
                .build();

        beerOrder = BeerOrder.builder()
                .id(1)
                .orderStatus(BeerOrder.OrderStatus.NEW)
                .customer(customer)
                .build();

        beerOrderDto = BeerOrderDto.builder()
                .id(1)
                .orderStatus("NEW")
                .customerId(1)
                .build();

        BeerOrderLineCommand lineCommand = BeerOrderLineCommand.builder()
                .beerId(1)
                .orderQuantity(10)
                .build();

        createCommand = CreateBeerOrderCommand.builder()
                .customerId(1)
                .beerOrderLines(Collections.singletonList(lineCommand))
                .build();
    }

    @Test
    void createBeerOrder_ShouldReturnBeerOrderDto() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(beerRepository.findById(anyInt())).thenReturn(Optional.of(beer));
        when(beerOrderRepository.save(any(BeerOrder.class))).thenReturn(beerOrder);
        when(beerOrderMapper.beerOrderToBeerOrderDto(any(BeerOrder.class))).thenReturn(beerOrderDto);
        
        // Mock the beerOrderLineMapper.beerOrderLineCommandToBeerOrderLine method
        when(beerOrderLineMapper.beerOrderLineCommandToBeerOrderLine(any(), any())).thenReturn(
            com.cherylorcutt.juniemvc.entities.BeerOrderLine.builder()
                .id(1)
                .orderQuantity(10)
                .beer(beer)
                .build()
        );

        // when
        BeerOrderDto result = beerOrderService.createBeerOrder(createCommand);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getOrderStatus()).isEqualTo("NEW");
        verify(beerOrderRepository).save(any(BeerOrder.class));
    }

    @Test
    void createBeerOrder_ShouldThrowCustomerNotFoundException_WhenCustomerDoesNotExist() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> beerOrderService.createBeerOrder(createCommand))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with ID: 1");
    }

    @Test
    void createBeerOrder_ShouldThrowBeerNotFoundException_WhenBeerDoesNotExist() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(beerRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> beerOrderService.createBeerOrder(createCommand))
                .isInstanceOf(BeerNotFoundException.class)
                .hasMessageContaining("Beer not found with ID: 1");
    }

    @Test
    void getBeerOrderById_ShouldReturnBeerOrderDto_WhenOrderExists() {
        // given
        when(beerOrderRepository.findById(anyInt())).thenReturn(Optional.of(beerOrder));
        when(beerOrderMapper.beerOrderToBeerOrderDto(any(BeerOrder.class))).thenReturn(beerOrderDto);

        // when
        Optional<BeerOrderDto> result = beerOrderService.getBeerOrderById(1);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
    }

    @Test
    void getBeerOrderById_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
        // given
        when(beerOrderRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> beerOrderService.getBeerOrderById(1))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with ID: 1");
    }

    @Test
    void getAllBeerOrders_ShouldReturnAllOrders() {
        // given
        BeerOrder order2 = BeerOrder.builder()
                .id(2)
                .orderStatus(BeerOrder.OrderStatus.PROCESSING)
                .customer(customer)
                .build();

        BeerOrderDto orderDto2 = BeerOrderDto.builder()
                .id(2)
                .orderStatus("PROCESSING")
                .customerId(1)
                .build();

        when(beerOrderRepository.findAll()).thenReturn(Arrays.asList(beerOrder, order2));
        when(beerOrderMapper.beerOrderToBeerOrderDto(beerOrder)).thenReturn(beerOrderDto);
        when(beerOrderMapper.beerOrderToBeerOrderDto(order2)).thenReturn(orderDto2);

        // when
        List<BeerOrderDto> result = beerOrderService.getAllBeerOrders();

        // then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(BeerOrderDto::getId).containsExactly(1, 2);
    }

    @Test
    void getBeerOrdersByCustomerId_ShouldReturnCustomerOrders() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.of(customer));
        when(beerOrderRepository.findAllByCustomer(any(Customer.class))).thenReturn(Collections.singletonList(beerOrder));
        when(beerOrderMapper.beerOrderToBeerOrderDto(any(BeerOrder.class))).thenReturn(beerOrderDto);

        // when
        List<BeerOrderDto> result = beerOrderService.getBeerOrdersByCustomerId(1);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1);
    }

    @Test
    void getBeerOrdersByCustomerId_ShouldThrowCustomerNotFoundException_WhenCustomerDoesNotExist() {
        // given
        when(customerRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> beerOrderService.getBeerOrdersByCustomerId(1))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessageContaining("Customer not found with ID: 1");
    }

    @Test
    void cancelBeerOrder_ShouldReturnTrue_WhenOrderCanBeCancelled() {
        // given
        when(beerOrderRepository.findById(anyInt())).thenReturn(Optional.of(beerOrder));

        // when
        boolean result = beerOrderService.cancelBeerOrder(1);

        // then
        assertThat(result).isTrue();
        assertThat(beerOrder.getOrderStatus()).isEqualTo(BeerOrder.OrderStatus.CANCELLED);
        verify(beerOrderRepository).save(beerOrder);
    }

    @Test
    void cancelBeerOrder_ShouldThrowOrderNotFoundException_WhenOrderDoesNotExist() {
        // given
        when(beerOrderRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when/then
        assertThatThrownBy(() -> beerOrderService.cancelBeerOrder(1))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessageContaining("Order not found with ID: 1");
    }

    @Test
    void cancelBeerOrder_ShouldThrowInvalidOrderStateException_WhenOrderCannotBeCancelled() {
        // given
        BeerOrder completedOrder = BeerOrder.builder()
                .id(1)
                .orderStatus(BeerOrder.OrderStatus.COMPLETED)
                .customer(customer)
                .build();
        when(beerOrderRepository.findById(anyInt())).thenReturn(Optional.of(completedOrder));

        // when/then
        assertThatThrownBy(() -> beerOrderService.cancelBeerOrder(1))
                .isInstanceOf(InvalidOrderStateException.class)
                .hasMessageContaining("Cannot transition order from COMPLETED to CANCELLED");
    }
}