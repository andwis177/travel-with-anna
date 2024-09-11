package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Trip Controller tests")
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TripMgr tripFacade;

    private User user;


    @BeforeEach
    void setup() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrivedRole = existingRole.orElseGet(() -> roleRepository.save(role));



        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .build();
        user.setAccountLocked(false);
        user.setEnabled(true);

        Role adminRole = new Role();
        adminRole.setRoleName(getAdminRole());
        adminRole.setAuthority(getAdminAuthority());
        Optional<Role> existingAdminRole = roleRepository.findByRoleName(getAdminRole());
        Role retrivedAdminRole = existingAdminRole.orElseGet(() -> roleRepository.save(adminRole));

        User adminUser = User.builder()
                .userName("adminUserName")
                .email("adminEmail@example.com")
                .password(passwordEncoder.encode("adminPassword"))
                .role(retrivedAdminRole)
                .avatarId(2L)

                .build();
        adminUser.setAccountLocked(false);
        adminUser.setEnabled(true);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        tripRepository.deleteAll();
    }


    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void createTrip_ShouldReturnCreated() throws Exception {
        TripCreatorRequest trip = new TripCreatorRequest("Trip name", "EUR", new BigDecimal("1000"));
        Long tripId = 1L;
        when(tripFacade.createTrip(trip, createAuthentication(user))).thenReturn(tripId);
        String jsonContent = objectMapper.writeValueAsString(trip);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/trip/create")
                        .principal(createAuthentication(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(jsonContent))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getAllOwnersTrips_ShouldReturnOk() throws Exception {
        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/trip")
                        .principal(createAuthentication(user))
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getTripById_ShouldReturnOk() throws Exception {
        // Given
        Long tripId = 1L;

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .get("/trip/{tripId}", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void changeTripName_ShouldReturnOk() throws Exception {
        // Given
        Long tripId = 1L;
        String newTripName = "New Trip Name";

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .patch("/trip/{tripId}/name/change", tripId)
                        .param("tripName", newTripName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void deleteTrip_ShouldReturnNoContent() throws Exception {
        // Given
        Long tripId = 1L;

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/trip/{tripId}/delete", tripId)
                        .principal(createAuthentication(user))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}

