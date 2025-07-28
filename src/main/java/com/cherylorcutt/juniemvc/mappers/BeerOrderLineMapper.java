package com.cherylorcutt.juniemvc.mappers;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.entities.BeerOrderLine;
import com.cherylorcutt.juniemvc.models.BeerOrderLineDto;
import com.cherylorcutt.juniemvc.models.commands.BeerOrderLineCommand;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BeerOrderLineMapper {
    
    @Mapping(target = "beerId", source = "beer.id")
    @Mapping(target = "beerName", source = "beer.beerName")
    @Mapping(target = "beerStyle", source = "beer.beerStyle")
    @Mapping(target = "upc", source = "beer.upc")
    BeerOrderLineDto beerOrderLineToBeerOrderLineDto(BeerOrderLine beerOrderLine);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "beerOrder", ignore = true)
    @Mapping(target = "beer", source = "beer")
    BeerOrderLine beerOrderLineDtoToBeerOrderLine(BeerOrderLineDto beerOrderLineDto, Beer beer);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    @Mapping(target = "beerOrder", ignore = true)
    @Mapping(target = "beer", source = "beer")
    BeerOrderLine beerOrderLineCommandToBeerOrderLine(BeerOrderLineCommand command, Beer beer);
}