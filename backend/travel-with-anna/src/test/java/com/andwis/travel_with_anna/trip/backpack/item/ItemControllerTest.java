package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@DisplayName("Item Controller tests")
class ItemControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ItemFacade itemFacade() {
            return Mockito.mock(ItemFacade.class);
        }
    }

    @Autowired
    private ItemFacade itemFacade;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        Role role = Role.builder()
                .roleName(USER.getRoleName())
                .roleAuthority(USER.getAuthority())
                .build();
        roleRepository.save(role);

        Trip trip = Trip.builder()
                .tripName("tripName")
                .build();

        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);
        user.addTrip(trip);

        User secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(2L)
                .build();
        secondaryUser.setEnabled(true);
        userRepository.save(secondaryUser);
    }

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
        when(itemFacade.getAllItemsByBackpackId(eq(backpackId), any())).thenReturn(items);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/item/{backpackId}", backpackId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(items)));
    }

    private static @NotNull ItemResponse getItemResponse() {
        ExpanseResponse expanseResponse = ExpanseResponse.builder()
                .expanseId(1L)
                .expanseName("Camping Fee")
                .expanseCategory("Accommodation")
                .date("2022-01-01")
                .currency("USD")
                .price(new BigDecimal("20.00"))
                .paid(new BigDecimal("0.00"))
                .exchangeRate(new BigDecimal("1.00"))
                .priceInTripCurrency(new BigDecimal("20.00"))
                .paidInTripCurrency(new BigDecimal("0.00"))
                .build();

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
        when(itemFacade.getAllItemsByBackpackId(eq(nonExistentBackpackId), any())).
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
        ItemResponse itemRequest1 = new ItemResponse(
                1L,
                "Water Bottle",
                "2",
                true,
                null);

        ItemResponse itemRequest2 = new ItemResponse(
                2L,
                "Sleeping Bag",
                "1",
                false,
                null);

        List<ItemResponse> items = List.of(itemRequest1, itemRequest2);
        String requestBody = objectMapper.writeValueAsString(items);
        doNothing().when(itemFacade).saveAllItems(eq(items), any());

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/item/save-list")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}