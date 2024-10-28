//package com.andwis.travel_with_anna.trip.backpack.item;
//
//import com.andwis.travel_with_anna.handler.exception.ItemNotFoundException;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@SpringBootTest
//@DisplayName("Item Service tests")
//class ItemServiceTest {
//    @Autowired
//    private ItemService itemService;
//
//    @Autowired
//    private ItemRepository itemRepository;
//
//    private Item item;
//    private ItemWithExpanseRequest itemWithExpanseRequest;
//
//    @BeforeEach
//    void setUp() {
//        item = Item.builder()
//                .itemName("Flight Ticket")
//                .quantity("1")
//                .build();
//
//        itemWithExpanseRequest = ItemWithExpanseRequest.builder()
//                .itemName("Flight Ticket")
//                .qty("1")
//                .isPacked(false)
//                .build();
//        itemRepository.save(item);
//    }
//
//    @AfterEach
//    void tearDown() {
//        itemRepository.deleteAll();
//    }
//
//    @Test
//    @Transactional
//    void testSaveItem() {
//        // Given
//        Item newItem = Item.builder()
//                .itemName("Hotel Booking")
//                .quantity("2")
//                .build();
//
//        // When
//        itemService.saveItem(newItem);
//
//        // Then
//        Item savedItem = itemRepository.findById(newItem.getItemId()).orElseThrow();
//        assertNotNull(savedItem);
//        assertEquals("Hotel Booking", savedItem.getItemName());
//        assertEquals("2", savedItem.getQuantity());
//    }
//
//    @Test
//    @Transactional
//    void testFindByIdWithValidId() {
//        // Given
//        // When
//        Item foundItem = itemService.findById(item.getItemId());
//
//        // Then
//        assertNotNull(foundItem);
//        assertEquals(item.getItemId(), foundItem.getItemId());
//        assertEquals("Flight Ticket", foundItem.getItemName());
//    }
//
//    @Test
//    @Transactional
//    void testFindByIdWithInvalidId() {
//        // Given
//        // When
//        Long invalidId = 999L;
//
//        // Then
//        assertThrows(ItemNotFoundException.class, () -> itemService.findById(invalidId));
//    }
//
//    @Test
//    @Transactional
//    void testCreateItem() {
//        // Given
//        // When
//        Item createdItem = itemService.createItem(itemWithExpanseRequest);
//
//        // Then
//        assertNotNull(createdItem);
//        assertEquals("Flight Ticket", createdItem.getItemName());
//        assertEquals("1", createdItem.getQuantity());
//        assertFalse(createdItem.isPacked());
//    }
//
//    @Test
//    @Transactional
//    void testGetAllItemsByBackpackId() {
//        // Given
//        Long backpackId = 1L;
//
//        // When
//        List<ItemResponse> itemResponse = itemService.getAllItemsByBackpackId(backpackId);
//
//        // Then
//        assertNotNull(itemResponse);
//    }
//
//    @Test
//    @Transactional
//    void testSaveAllItems() {
//        // Given
//        ItemRequest itemRequest = new ItemRequest(item.getItemId(), "Updated Flight Ticket", "2", false);
//        List<ItemRequest> itemRequests = List.of(itemRequest);
//
//        // When
//        itemService.saveAllItems(itemRequests);
//
//        // Then
//        Item updatedItem = itemRepository.findById(item.getItemId()).orElseThrow();
//        assertEquals("Updated Flight Ticket", updatedItem.getItemName());
//        assertEquals("2", updatedItem.getQuantity());
//    }
//
//    @Test
//    @Transactional
//    void testDeleteItem() {
//        // Given
//        Long itemId = item.getItemId();
//
//        // When
//        itemService.deleteItem(itemId);
//
//        // Then
//        assertFalse(itemRepository.findById(itemId).isPresent());
//    }
//}