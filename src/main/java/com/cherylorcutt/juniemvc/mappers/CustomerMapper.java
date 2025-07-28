package com.cherylorcutt.juniemvc.mappers;

import com.cherylorcutt.juniemvc.entities.Customer;
import com.cherylorcutt.juniemvc.models.CustomerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {
    
    CustomerDto customerToCustomerDto(Customer customer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "beerOrders", ignore = true)
    Customer customerDtoToCustomer(CustomerDto customerDto);
}