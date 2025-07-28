package com.cherylorcutt.juniemvc.mappers;

import com.cherylorcutt.juniemvc.entities.BeerOrder;
import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.models.BeerOrderDto;
import com.cherylorcutt.juniemvc.models.commands.CreateBeerOrderCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BeerOrderLineMapper.class})
public interface BeerOrderMapper {
    
    @Mapping(target = "customerId", source = "customer.id")
    BeerOrderDto beerOrderToBeerOrderDto(BeerOrder beerOrder);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "customer", source = "customer")
    BeerOrder beerOrderDtoToBeerOrder(BeerOrderDto beerOrderDto, Customer customer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "orderStatus", constant = "NEW")
    @Mapping(target = "customer", source = "customer")
    @Mapping(target = "beerOrderLines", source = "command.beerOrderLines")
    @Mapping(target = "orderStatusCallbackUrl", source = "command.orderStatusCallbackUrl")
    BeerOrder createBeerOrderCommandToBeerOrder(CreateBeerOrderCommand command, Customer customer);
}