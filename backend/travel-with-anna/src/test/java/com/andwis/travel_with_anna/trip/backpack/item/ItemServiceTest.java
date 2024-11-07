package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.handler.exception.ItemNotFoundException;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Item Service tests")
class ItemServiceTest {
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private EntityManager entityManager;
    private Item item;

    @BeforeEach
    void setUp() {
        item = Item.builder()
                .itemName("Flight Ticket")
                .quantity("1")
                .build();

        Expanse expanse = Expanse.builder()
                .expanseName("Flight")
                .currency("USD")
                .price(BigDecimal.valueOf(1000))
                .paid(BigDecimal.valueOf(100))
                .exchangeRate(BigDecimal.valueOf(2.0))
                .build();

        item.setExpanse(expanse);
        itemRepository.save(item);
    }

    @AfterEach
    void tearDown() {
        itemRepository.deleteAll();
    }

    @Test
    @Transactional
    void testSaveItem() {
        // Given
        Item newItem = Item.builder()
                .itemName("Hotel Booking")
                .quantity("2")
                .build();

        // When
        itemService.saveItem(newItem);

        // Then
        Item savedItem = itemRepository.findById(newItem.getItemId()).orElseThrow();
        assertNotNull(savedItem);
        assertEquals("Hotel Booking", savedItem.getItemName());
        assertEquals("2", savedItem.getQuantity());
    }

    @Test
    @Transactional
    void testFindByIdWithValidId() {
        // Given
        // When
        Item foundItem = itemService.findById(item.getItemId());

        // Then
        assertNotNull(foundItem);
        assertEquals(item.getItemId(), foundItem.getItemId());
        assertEquals("Flight Ticket", foundItem.getItemName());
    }

    @Test
    @Transactional
    void testFindByIdWithInvalidId() {
        // Given
        Long invalidId = 999L;

        // When & Then
        assertThrows(ItemNotFoundException.class, () -> itemService.findById(invalidId));
    }

    @Test
    @Transactional
    void testCreateItem() {
        // Given
        ItemWithExpanseRequest request = ItemWithExpanseRequest.builder()
                .itemName("Tent")
                .qty("1")
                .isPacked(true)
                .build();

        // When
        Item createdItem = itemService.createItem(request);

        // Then
        assertNotNull(createdItem);
        assertEquals("Tent", createdItem.getItemName());
        assertEquals("1", createdItem.getQuantity());
        assertTrue(createdItem.isPacked());
    }

    @Test
    @Transactional
    void testCreateItemWithLongItemName() {
        // Given
        ItemWithExpanseRequest longNameRequest = ItemWithExpanseRequest.builder()
                .itemName("A very long item name that exceeds the sixty character limit enforced by validation")
                .qty("1")
                .isPacked(false)
                .build();

        // When
        Item createdItem = itemService.createItem(longNameRequest);

        // Then
        assertNotNull(createdItem);
        assertEquals("A very long item name that exceeds the sixty character limit enforced by validation", createdItem.getItemName());
        assertEquals("1", createdItem.getQuantity());
        assertFalse(createdItem.isPacked());
    }

    @Test
    @Transactional
    void testGetAllItemsByBackpackId() {
        // Given
        Long backpackId = 1L;

        // When
        List<ItemResponse> itemResponses = itemService.getAllItemsByBackpackId(backpackId);

        // Then
        assertNotNull(itemResponses);
    }

    @Test
    @Transactional
    void testSaveAllItems() {
        // Given
        ItemRequest itemRequest = new ItemRequest(
                item.getItemId(), "Updated Flight Ticket", "2", false);
        List<ItemRequest> itemRequests = List.of(itemRequest);

        // When
        itemService.saveAllItems(itemRequests);

        // Then
        Item updatedItem = itemRepository.findById(item.getItemId()).orElseThrow();
        assertEquals("Updated Flight Ticket", updatedItem.getItemName());
        assertEquals("2", updatedItem.getQuantity());
    }

    @Test
    @Transactional
    void testSaveAllItems_MultipleItems() {
        // Given
        Expanse expanse2 = Expanse.builder()
                .expanseName("Dinner")
                .currency("PLN")
                .price(BigDecimal.valueOf(6000))
                .paid(BigDecimal.valueOf(600))
                .exchangeRate(BigDecimal.valueOf(0.5))
                .build();

        Item item2 = Item.builder().itemName("Passport").quantity("1").build();
        item2.addExpanse(expanse2);
        itemRepository.save(item2);

        ItemRequest request1 = new ItemRequest(
                item.getItemId(), "Updated Ticket", "3", false);
        ItemRequest request2 = new ItemRequest(
                item2.getItemId(), "Passport", "1", true);
        List<ItemRequest> requests = List.of(request1, request2);

        // When
        itemService.saveAllItems(requests);

        // Then
        Item updatedItem1 = itemRepository.findById(item.getItemId()).orElseThrow();
        entityManager.flush();
        Item updatedItem2 = itemRepository.findById(item2.getItemId()).orElseThrow();
        entityManager.flush();

        assertEquals("Updated Ticket", updatedItem1.getItemName());
        assertEquals("3", updatedItem1.getQuantity());
        assertFalse(updatedItem1.isPacked());
        assertEquals("Flight", updatedItem1.getExpanse().getExpanseName());
        assertEquals("USD", updatedItem1.getExpanse().getCurrency());
        assertEquals(0, updatedItem1.getExpanse().getPrice().compareTo(BigDecimal.valueOf(1000.00)));
        assertEquals(0, updatedItem1.getExpanse().getPaid().compareTo(BigDecimal.valueOf(100.00)));
        assertEquals(0, updatedItem1.getExpanse().getExchangeRate().compareTo(BigDecimal.valueOf(2.00)));
        assertEquals(0, updatedItem1.getExpanse().getPriceInTripCurrency().compareTo(BigDecimal.valueOf(2000.00)));
        assertEquals(0, updatedItem1.getExpanse().getPaidInTripCurrency().compareTo(BigDecimal.valueOf(200.00)));

        assertEquals("Passport", updatedItem2.getItemName());
        assertEquals("1", updatedItem2.getQuantity());
        assertTrue(updatedItem2.isPacked());
        assertEquals("Dinner", updatedItem2.getExpanse().getExpanseName());
        assertEquals("PLN", updatedItem2.getExpanse().getCurrency());
        assertEquals(0, updatedItem2.getExpanse().getPrice().compareTo(BigDecimal.valueOf(6000)));
        assertEquals(0, updatedItem2.getExpanse().getPaid().compareTo(BigDecimal.valueOf(600)));
        assertEquals(0, updatedItem2.getExpanse().getExchangeRate().compareTo(BigDecimal.valueOf(0.5)));
        assertEquals(0, updatedItem2.getExpanse().getPriceInTripCurrency().compareTo(BigDecimal.valueOf(3000)));
        assertEquals(0, updatedItem2.getExpanse().getPaidInTripCurrency().compareTo(BigDecimal.valueOf(300)));
    }

    @Test
    @Transactional
    void testDeleteItem() {
        // Given
        Long itemId = item.getItemId();

        // When
        itemService.deleteItem(itemId);

        // Then
        assertFalse(itemRepository.findById(itemId).isPresent());
    }
}