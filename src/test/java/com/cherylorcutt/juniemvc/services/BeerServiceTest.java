package com.cherylorcutt.juniemvc.services;

import com.cherylorcutt.juniemvc.entities.Beer;
import com.cherylorcutt.juniemvc.mappers.BeerMapper;
import com.cherylorcutt.juniemvc.models.BeerDto;
import com.cherylorcutt.juniemvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BeerServiceTest {

    @Mock
    private BeerRepository beerRepository;

    @Mock
    private BeerMapper beerMapper;

    @InjectMocks
    private BeerServiceImpl beerService;

    private Beer testBeer;
    private BeerDto testBeerDto;
    private List<Beer> testBeers;
    private List<BeerDto> testBeerDtos;

    @BeforeEach
    void setUp() {
        // Set up test Beer entity
        testBeer = Beer.builder()
                .id(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .upc("123456")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(100)
                .build();

        Beer testBeer2 = Beer.builder()
                .id(2)
                .beerName("Another Beer")
                .beerStyle("Stout")
                .upc("654321")
                .price(new BigDecimal("14.99"))
                .quantityOnHand(200)
                .build();

        testBeers = Arrays.asList(testBeer, testBeer2);

        // Set up test BeerDto
        testBeerDto = BeerDto.builder()
                .id(1)
                .beerName("Test Beer")
                .beerStyle("IPA")
                .upc("123456")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(100)
                .build();

        BeerDto testBeerDto2 = BeerDto.builder()
                .id(2)
                .beerName("Another Beer")
                .beerStyle("Stout")
                .upc("654321")
                .price(new BigDecimal("14.99"))
                .quantityOnHand(200)
                .build();

        testBeerDtos = Arrays.asList(testBeerDto, testBeerDto2);
    }

    @Test
    void testSaveBeerCreate() {
        // Given
        BeerDto beerDtoToSave = BeerDto.builder()
                .beerName("New Beer")
                .beerStyle("Lager")
                .upc("789012")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(50)
                .build();

        Beer beerToSave = Beer.builder()
                .beerName("New Beer")
                .beerStyle("Lager")
                .upc("789012")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(50)
                .build();

        Beer savedBeer = Beer.builder()
                .id(3)
                .beerName("New Beer")
                .beerStyle("Lager")
                .upc("789012")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(50)
                .build();

        BeerDto savedBeerDto = BeerDto.builder()
                .id(3)
                .beerName("New Beer")
                .beerStyle("Lager")
                .upc("789012")
                .price(new BigDecimal("10.99"))
                .quantityOnHand(50)
                .build();

        when(beerMapper.beerDtoToBeer(beerDtoToSave)).thenReturn(beerToSave);
        when(beerRepository.save(any(Beer.class))).thenReturn(savedBeer);
        when(beerMapper.beerToBeerDto(savedBeer)).thenReturn(savedBeerDto);

        // When
        BeerDto result = beerService.saveBeer(beerDtoToSave);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(3);
        assertThat(result.getBeerName()).isEqualTo("New Beer");
        verify(beerMapper).beerDtoToBeer(beerDtoToSave);
        verify(beerRepository).save(any(Beer.class));
        verify(beerMapper).beerToBeerDto(savedBeer);
    }

    @Test
    void testSaveBeerUpdate() {
        // Given
        BeerDto beerDtoToUpdate = BeerDto.builder()
                .id(1)
                .beerName("Updated Beer")
                .beerStyle("Pale Ale")
                .upc("123456")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(150)
                .build();

        Beer beerToUpdate = Beer.builder()
                .beerName("Updated Beer")
                .beerStyle("Pale Ale")
                .upc("123456")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(150)
                .build();

        Beer updatedBeer = Beer.builder()
                .id(1)
                .beerName("Updated Beer")
                .beerStyle("Pale Ale")
                .upc("123456")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(150)
                .build();

        BeerDto updatedBeerDto = BeerDto.builder()
                .id(1)
                .beerName("Updated Beer")
                .beerStyle("Pale Ale")
                .upc("123456")
                .price(new BigDecimal("13.99"))
                .quantityOnHand(150)
                .build();

        when(beerMapper.beerDtoToBeer(beerDtoToUpdate)).thenReturn(beerToUpdate);
        when(beerRepository.save(any(Beer.class))).thenReturn(updatedBeer);
        when(beerMapper.beerToBeerDto(updatedBeer)).thenReturn(updatedBeerDto);

        // When
        BeerDto result = beerService.saveBeer(beerDtoToUpdate);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getBeerName()).isEqualTo("Updated Beer");
        verify(beerMapper).beerDtoToBeer(beerDtoToUpdate);
        verify(beerRepository).save(any(Beer.class));
        verify(beerMapper).beerToBeerDto(updatedBeer);
    }

    @Test
    void testGetBeerByIdFound() {
        // Given
        when(beerRepository.findById(1)).thenReturn(Optional.of(testBeer));
        when(beerMapper.beerToBeerDto(testBeer)).thenReturn(testBeerDto);

        // When
        Optional<BeerDto> result = beerService.getBeerById(1);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(1);
        assertThat(result.get().getBeerName()).isEqualTo("Test Beer");
        verify(beerRepository).findById(1);
        verify(beerMapper).beerToBeerDto(testBeer);
    }

    @Test
    void testGetBeerByIdNotFound() {
        // Given
        when(beerRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<BeerDto> result = beerService.getBeerById(999);

        // Then
        assertThat(result).isEmpty();
        verify(beerRepository).findById(999);
        verify(beerMapper, never()).beerToBeerDto(any(Beer.class));
    }

    @Test
    void testGetAllBeers() {
        // Given
        when(beerRepository.findAll()).thenReturn(testBeers);
        when(beerMapper.beerToBeerDto(testBeers.get(0))).thenReturn(testBeerDtos.get(0));
        when(beerMapper.beerToBeerDto(testBeers.get(1))).thenReturn(testBeerDtos.get(1));

        // When
        List<BeerDto> result = beerService.getAllBeers();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.get(0).getBeerName()).isEqualTo("Test Beer");
        assertThat(result.get(1).getBeerName()).isEqualTo("Another Beer");
        verify(beerRepository).findAll();
        verify(beerMapper, times(2)).beerToBeerDto(any(Beer.class));
    }

    @Test
    void testDeleteBeerByIdFound() {
        // Given
        when(beerRepository.existsById(1)).thenReturn(true);
        doNothing().when(beerRepository).deleteById(1);

        // When
        boolean result = beerService.deleteBeerById(1);

        // Then
        assertThat(result).isTrue();
        verify(beerRepository).existsById(1);
        verify(beerRepository).deleteById(1);
    }

    @Test
    void testDeleteBeerByIdNotFound() {
        // Given
        when(beerRepository.existsById(999)).thenReturn(false);

        // When
        boolean result = beerService.deleteBeerById(999);

        // Then
        assertThat(result).isFalse();
        verify(beerRepository).existsById(999);
        verify(beerRepository, never()).deleteById(anyInt());
    }
}