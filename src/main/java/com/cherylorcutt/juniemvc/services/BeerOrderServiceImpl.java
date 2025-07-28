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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
class BeerOrderServiceImpl implements BeerOrderService {

    private static final Logger log = LoggerFactory.getLogger(BeerOrderServiceImpl.class);
    
    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerRepository beerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderLineMapper beerOrderLineMapper;

    BeerOrderServiceImpl(BeerOrderRepository beerOrderRepository,
                        CustomerRepository customerRepository,
                        BeerRepository beerRepository,
                        BeerOrderMapper beerOrderMapper,
                        BeerOrderLineMapper beerOrderLineMapper) {
        this.beerOrderRepository = beerOrderRepository;
        this.customerRepository = customerRepository;
        this.beerRepository = beerRepository;
        this.beerOrderMapper = beerOrderMapper;
        this.beerOrderLineMapper = beerOrderLineMapper;
    }

    @Override
    @Transactional
    public BeerOrderDto createBeerOrder(CreateBeerOrderCommand command) {
        log.debug("Creating beer order for customer ID: {}", command.getCustomerId());
        
        Customer customer = customerRepository.findById(command.getCustomerId())
                .orElseThrow(() -> new CustomerNotFoundException(command.getCustomerId()));
        
        // Create a new BeerOrder without beer order lines
        BeerOrder beerOrder = BeerOrder.builder()
                .customer(customer)
                .orderStatus(BeerOrder.OrderStatus.NEW)
                .orderStatusCallbackUrl(command.getOrderStatusCallbackUrl())
                .build();
        
        // Add beer order lines
        command.getBeerOrderLines().forEach(lineCommand -> {
            Beer beer = getBeerForOrderLine(lineCommand);
            beerOrder.addBeerOrderLine(beerOrderLineMapper
                    .beerOrderLineCommandToBeerOrderLine(lineCommand, beer));
        });
        
        BeerOrder savedBeerOrder = beerOrderRepository.save(beerOrder);
        
        log.debug("Created beer order with ID: {}", savedBeerOrder.getId());
        return beerOrderMapper.beerOrderToBeerOrderDto(savedBeerOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BeerOrderDto> getBeerOrderById(Integer id) {
        log.debug("Getting beer order by ID: {}", id);
        return Optional.ofNullable(beerOrderRepository.findById(id)
                .map(beerOrderMapper::beerOrderToBeerOrderDto)
                .orElseThrow(() -> new OrderNotFoundException(id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeerOrderDto> getAllBeerOrders() {
        log.debug("Getting all beer orders");
        return beerOrderRepository.findAll().stream()
                .map(beerOrderMapper::beerOrderToBeerOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BeerOrderDto> getBeerOrdersByCustomerId(Integer customerId) {
        log.debug("Getting beer orders for customer ID: {}", customerId);
        
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
        
        return beerOrderRepository.findAllByCustomer(customer).stream()
                .map(beerOrderMapper::beerOrderToBeerOrderDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public boolean cancelBeerOrder(Integer id) {
        log.debug("Cancelling beer order with ID: {}", id);
        
        BeerOrder beerOrder = beerOrderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        
        // Only orders in NEW, PENDING, or PROCESSING state can be cancelled
        if (beerOrder.getOrderStatus() == BeerOrder.OrderStatus.NEW ||
            beerOrder.getOrderStatus() == BeerOrder.OrderStatus.PENDING ||
            beerOrder.getOrderStatus() == BeerOrder.OrderStatus.PROCESSING) {
            
            beerOrder.setOrderStatus(BeerOrder.OrderStatus.CANCELLED);
            beerOrderRepository.save(beerOrder);
            log.debug("Beer order with ID: {} has been cancelled", id);
            return true;
        } else {
            log.debug("Cannot cancel beer order with ID: {} because it is in {} state", 
                    id, beerOrder.getOrderStatus());
            throw new InvalidOrderStateException(beerOrder.getOrderStatus(), BeerOrder.OrderStatus.CANCELLED);
        }
    }
    
    private Beer getBeerForOrderLine(BeerOrderLineCommand lineCommand) {
        return beerRepository.findById(lineCommand.getBeerId())
                .orElseThrow(() -> new BeerNotFoundException(lineCommand.getBeerId()));
    }
}