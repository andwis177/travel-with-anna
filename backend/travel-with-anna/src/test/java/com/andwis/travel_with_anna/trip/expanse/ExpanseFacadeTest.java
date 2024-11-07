package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ExpanseFacadeTest {
    @Mock
    private ExpanseService expanseService;
    @Mock
    private ItemService itemService;
    @Mock
    private ActivityService activityService;
    @InjectMocks
    private ExpanseFacade expanseFacade;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrUpdateExpanse_Item() {
        // Given
        ExpanseRequest request = ExpanseRequest.builder()
                .entityId(1L)
                .entityType("item")
                .price(BigDecimal.valueOf(100))
                .paid(BigDecimal.valueOf(50))
                .exchangeRate(BigDecimal.valueOf(1.2))
                .build();
        Item mockItem = new Item();
        when(itemService.findById(request.getEntityId())).thenReturn(mockItem);
        when(expanseService.createOrUpdateExpanse(any(), any(), any(), any()))
                .thenReturn(new ExpanseResponse(
                        1L, "Test Expanse", "USD", BigDecimal.valueOf(100),
                        BigDecimal.valueOf(50), BigDecimal.valueOf(1.2), BigDecimal.valueOf(120), BigDecimal.valueOf(60)));

        // When
        ExpanseResponse response = expanseFacade.createOrUpdateExpanse(request);

        // Then
        assertNotNull(response);
        assertEquals("Test Expanse", response.expanseName());
        verify(expanseService, times(1)).createOrUpdateExpanse(any(), any(), any(), any());
    }

    @Test
    void testCreateOrUpdateExpanse_Activity() {
        // Given
        ExpanseRequest request = ExpanseRequest.builder()
                .entityId(1L)
                .entityType("activity")
                .price(BigDecimal.valueOf(150))
                .paid(BigDecimal.valueOf(70))
                .exchangeRate(BigDecimal.valueOf(1.3))
                .build();
        Activity mockActivity = new Activity();
        when(activityService.getById(request.getEntityId())).thenReturn(mockActivity);
        when(expanseService.createOrUpdateExpanse(any(), any(), any(), any()))
                .thenReturn(new ExpanseResponse(
                        1L, "Test Activity Expanse", "USD", BigDecimal.valueOf(150),
                        BigDecimal.valueOf(70), BigDecimal.valueOf(1.3), BigDecimal.valueOf(195), BigDecimal.valueOf(91)));

        // When
        ExpanseResponse response = expanseFacade.createOrUpdateExpanse(request);

        // Then
        assertNotNull(response);
        assertEquals("Test Activity Expanse", response.expanseName());
        verify(expanseService, times(1)).createOrUpdateExpanse(any(), any(), any(), any());
    }

    @Test
    void testGetExpanseByEntityId_Item() {
        // Given
        Long entityId = 1L;
        String entityType = "item";
        Item mockItem = new Item();
        when(itemService.findById(entityId)).thenReturn(mockItem);
        when(expanseService.getExpanseByEntityId(anyLong(), any(), any()))
                .thenReturn(new ExpanseResponse(1L, "Item Expanse", "USD",
                        BigDecimal.valueOf(100), BigDecimal.valueOf(50), BigDecimal.valueOf(1.2),
                        BigDecimal.valueOf(120), BigDecimal.valueOf(60)));

        // When
        ExpanseResponse response = expanseFacade.getExpanseByEntityId(entityId, entityType);

        // Then
        assertNotNull(response);
        assertEquals("Item Expanse", response.expanseName());
        verify(expanseService, times(1)).getExpanseByEntityId(anyLong(), any(), any());
    }

    @Test
    void testGetExpanseByEntityId_Activity() {
        // Given
        Long entityId = 2L;
        String entityType = "activity";
        Activity mockActivity = new Activity();
        when(activityService.getById(entityId)).thenReturn(mockActivity);
        when(expanseService.getExpanseByEntityId(anyLong(), any(), any()))
                .thenReturn(new ExpanseResponse(2L, "Activity Expanse", "USD",
                        BigDecimal.valueOf(200), BigDecimal.valueOf(80), BigDecimal.valueOf(1.5),
                        BigDecimal.valueOf(300), BigDecimal.valueOf(120)));

        // When
        ExpanseResponse response = expanseFacade.getExpanseByEntityId(entityId, entityType);

        // Then
        assertNotNull(response);
        assertEquals("Activity Expanse", response.expanseName());
        verify(expanseService, times(1)).getExpanseByEntityId(anyLong(), any(), any());
    }

    @Test
    void testGetExchangeRate() {
        // Given
        String currencyFrom = "USD";
        String currencyTo = "EUR";
        ExchangeResponse mockResponse = new ExchangeResponse("", BigDecimal.valueOf(0.85));
        when(expanseService.getExchangeRate(currencyFrom, currencyTo)).thenReturn(mockResponse);

        // When
        ExchangeResponse response = expanseFacade.getExchangeRate(currencyFrom, currencyTo);

        // Then
        assertNotNull(response);
        assertEquals(BigDecimal.valueOf(0.85), response.exchangeRate());
        verify(expanseService, times(1)).getExchangeRate(currencyFrom, currencyTo);
    }

    @Test
    void testGetExpanseInTripCurrency() {
        // Given
        BigDecimal price = BigDecimal.valueOf(100);
        BigDecimal paid = BigDecimal.valueOf(50);
        BigDecimal exchangeRate = BigDecimal.valueOf(1.2);
        ExpanseInTripCurrency mockExpanseInTripCurrency =
                new ExpanseInTripCurrency(BigDecimal.valueOf(120), BigDecimal.valueOf(60));
        when(expanseService.getExpanseInTripCurrency(price, paid, exchangeRate)).thenReturn(mockExpanseInTripCurrency);

        // When
        ExpanseInTripCurrency result = expanseFacade.getExpanseInTripCurrency(price, paid, exchangeRate);

        // Then
        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(120), result.price());
        assertEquals(BigDecimal.valueOf(60), result.paid());
        verify(expanseService, times(1)).getExpanseInTripCurrency(price, paid, exchangeRate);
    }
}