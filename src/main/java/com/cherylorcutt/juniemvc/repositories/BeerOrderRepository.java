package com.cherylorcutt.juniemvc.repositories;

import com.cherylorcutt.juniemvc.entities.BeerOrder;
import com.cherylorcutt.juniemvc.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BeerOrderRepository extends JpaRepository<BeerOrder, Integer> {
    List<BeerOrder> findAllByCustomer(Customer customer);
    List<BeerOrder> findAllByOrderStatus(BeerOrder.OrderStatus orderStatus);
    
    @Query("SELECT o FROM BeerOrder o JOIN FETCH o.beerOrderLines WHERE o.customer = :customer")
    List<BeerOrder> findAllByCustomerWithBeerOrderLines(@Param("customer") Customer customer);
}