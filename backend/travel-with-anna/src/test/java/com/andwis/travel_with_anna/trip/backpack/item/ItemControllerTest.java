package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Item Controller tests")
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ItemFacade itemFacade;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getAllItemsByBackpackId_ShouldReturnItems() throws Exception {
        // Given
        Long backpackId = 1L;
        ItemResponse itemResponse1 = getItemResponse();
        ItemResponse itemResponse2 = new ItemResponse(
                2L,
                "Sleeping Bag",
                "1",
                false,
                null
        );
        List<ItemResponse> items = List.of(itemResponse1, itemResponse2);
        when(itemFacade.getAllItemsByBackpackId(backpackId)).thenReturn(items);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/item/{backpackId}", backpackId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    private static @NotNull ItemResponse getItemResponse() {
        ExpanseResponse expanseResponse = new ExpanseResponse(
                1L,
                "Camping Fee",
                "USD",
                new BigDecimal("20.00"),
                new BigDecimal("0.00"),
                new BigDecimal("1.00"),
                new BigDecimal("20.00"),
                new BigDecimal("0.00")
        );

        return new ItemResponse(
                1L,
                "Water Bottle",
                "2",
                true,
                expanseResponse
        );
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getAllItemsByBackpackId_WithNonExistentId_ShouldReturnNotFound() throws Exception {
        // Given
        Long nonExistentBackpackId = 999L;
        when(itemFacade.getAllItemsByBackpackId(nonExistentBackpackId)).
                thenThrow(new BackpackNotFoundException("Backpack not found"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/item/{backpackId}", nonExistentBackpackId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void saveAllItemsFromTheList_ShouldReturnOk() throws Exception {
        // Given
        ItemRequest itemRequest1 = ItemRequest.builder()
                .itemId(1L)
                .itemName("Water Bottle")
                .qty("2")
                .isPacked(true)
                .build();

        ItemRequest itemRequest2 = ItemRequest.builder()
                .itemId(2L)
                .itemName("Sleeping Bag")
                .qty("1")
                .isPacked(false)
                .build();

        List<ItemRequest> items = List.of(itemRequest1, itemRequest2);
        String requestBody = objectMapper.writeValueAsString(items);
        doNothing().when(itemFacade).saveAllItems(anyList());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/item/save-list")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}