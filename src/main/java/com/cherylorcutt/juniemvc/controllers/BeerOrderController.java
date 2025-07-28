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