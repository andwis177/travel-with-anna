package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.ItemWithExpanseRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Backpack Controller Tests")
class BackpackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BackpackFacade backpackFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testAddItemToBackpack_ShouldReturnOk() throws Exception {
        // Given
        Long backpackId = 1L;
        ItemWithExpanseRequest itemWithExpanseRequest = ItemWithExpanseRequest.builder()
                .itemName("Tent")
                .qty("1")
                .isPacked(false)
                .build();

        String requestBody = objectMapper.writeValueAsString(itemWithExpanseRequest);

        doNothing().when(backpackFacade).addItemToBackpack(backpackId, itemWithExpanseRequest);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/backpack/{backpackId}/item-add", backpackId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getBackpackById_ShouldReturnBackpack() throws Exception {
        // Given
        Long backpackId = 1L;
        BackpackResponse backpackResponse = new BackpackResponse(backpackId, 1L, true);

        when(backpackFacade.getBackpackById(backpackId)).thenReturn(backpackResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/backpack/{backpackId}", backpackId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(backpackResponse)));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void deleteItem_ShouldReturnNoContent() throws Exception {
        // Given
        Long itemId = 2L;

        doNothing().when(backpackFacade).deleteItem(itemId);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/backpack/delete/{itemId}/item", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}