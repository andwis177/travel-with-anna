//package com.andwis.travel_with_anna.trip.backpack.item;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.anyList;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@DisplayName("Item Controller tests")
//class ItemControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private ItemFacade itemFacade;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    @WithMockUser(username = "email@example.com", authorities = "User")
//    void getAllItemsByBackpackId_ShouldReturnItems() throws Exception {
//        // Given
//        Long backpackId = 1L;
//
//        ItemResponse itemResponse1 =  new ItemResponse(
//                1L,
//                "Water Bottle",
//                "2",
//                true
//        );
//
//        ItemResponse itemResponse2 = new ItemResponse(
//                2L,
//                "Sleeping Bag",
//                "1",
//                false
//        );
//
//        List<ItemResponse> items = List.of(itemResponse1, itemResponse2);
//        when(itemFacade.getAllItemsByBackpackId(backpackId)).thenReturn(items);
//
//        // When & Then
//        mockMvc.perform(MockMvcRequestBuilders
//                        .get("/item/{backpackId}", backpackId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(content().json(objectMapper.writeValueAsString(items)));
//    }
//
//    @Test
//    @WithMockUser(username = "email@example.com", authorities = "User")
//    void saveAllItemsFromTheList_ShouldReturnOk() throws Exception {
//        // Given
//        ItemRequest itemRequest1 = ItemRequest.builder()
//                .itemId(1L)
//                .itemName("Water Bottle")
//                .qty("2")
//                .isPacked(true)
//                .build();
//
//        ItemRequest itemRequest2 = ItemRequest.builder()
//                .itemId(2L)
//                .itemName("Sleeping Bag")
//                .qty("1")
//                .isPacked(false)
//                .build();
//
//        List<ItemRequest> items = List.of(itemRequest1, itemRequest2);
//        String requestBody = objectMapper.writeValueAsString(items);
//
//        doNothing().when(itemFacade).saveAllItems(anyList());
//
//        // When & Then
//        mockMvc.perform(MockMvcRequestBuilders
//                        .patch("/item/save-list")
//                        .content(requestBody)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//}