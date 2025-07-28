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