package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.item.ItemRequest;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Backpack Controller Tests")
class BackpackControllerTest {
    @MockBean
    private BackpackFacade backpackFacade;
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
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByRoleName(USER.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(1, USER.getRoleName(), USER.getAuthority())));

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

        userDetails = createUserDetails(user);
    }

    @AfterEach()
    void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testAddItemToBackpack_ShouldReturnOk() throws Exception {
        // Given
        Long backpackId = 1L;
        ItemRequest itemRequest = ItemRequest.builder()
                .itemName("Tent")
                .quantity("1")
                .isPacked(false)
                .build();
        String requestBody = objectMapper.writeValueAsString(itemRequest);

        doNothing().when(backpackFacade).addItemToBackpack(backpackId, itemRequest, userDetails);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/backpack/{backpackId}/item", backpackId)
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getBackpackById_ShouldReturnBackpack() throws Exception {
        // Given
        Long backpackId = 1L;
        BackpackResponse backpackResponse = new BackpackResponse(backpackId, 1L, true);

        when(backpackFacade.getBackpackById(eq(backpackId), any())).thenReturn(backpackResponse);

        String jsonResponse = objectMapper.writeValueAsString(backpackResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/backpack/{backpackId}", backpackId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void deleteItem_ShouldReturnNoContent() throws Exception {
        // Given
        Long itemId = 2L;

        doNothing().when(backpackFacade).deleteItem(itemId, userDetails);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/backpack/{itemId}/item", itemId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private @NotNull UserDetails createUserDetails(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        securityUser,
                        user.getPassword(),
                        securityUser.getAuthorities()
                )
        );
        return securityUser;
    }
}