package com.cherylorcutt.juniemvc.mappers;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.models.BeerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for converting between Beer entity and BeerDto
 */
@Mapper(componentModel = "spring")
public interface BeerMapper {
    
    /**
     * Convert Beer entity to BeerDto
     * 
     * @param beer the Beer entity
     * @return the BeerDto
     */
    BeerDto beerToBeerDto(Beer beer);
    
    /**
     * Convert BeerDto to Beer entity
     * Ignores id, createdDate, and updateDate as these are managed by the persistence layer
     * 
     * @param beerDto the BeerDto
     * @return the Beer entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdDate", ignore = true)
    @Mapping(target = "updateDate", ignore = true)
    Beer beerDtoToBeer(BeerDto beerDto);
}