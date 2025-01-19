package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.day.DayGeneratorRequest;
import com.andwis.travel_with_anna.user.SecurityUser;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.mockito.Mockito.doNothing;
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
    private ObjectMapper objectMapper;

    private UserDetails userDetails;
    private User user;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public TripMgr tripMgr() {
            return Mockito.mock(TripMgr.class);
        }
    }

    @Autowired
    private TripMgr tripFacade;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = Role.builder()
                .roleName(USER.getRoleName())
                .roleAuthority(USER.getAuthority())
                .build();
        roleRepository.save(role);

        user = User.builder()
                .userName("adminUserName")
                .email("adminEmail@example.com")
                .password(passwordEncoder.encode("adminPassword"))
                .role(role)
                .avatarId(2L)
                .build();
        user.setAccountLocked(false);
        user.setEnabled(true);
        userDetails = createUserDetails(user);
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void createTrip_ShouldReturnCreated() throws Exception {
        TripCreatorRequest trip = new TripCreatorRequest("Trip name", "EUR", new BigDecimal("1000"));
        Long tripId = 1L;
        when(tripFacade.createTrip(trip, userDetails)).thenReturn(tripId);
        String jsonContent = objectMapper.writeValueAsString(trip);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/trip")
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
    void updateTrip_ShouldReturnOk() throws Exception {
        // Given
        DayGeneratorRequest dayGeneratorRequest = DayGeneratorRequest.builder()
                .associatedTripId(1L)
                .startDate(LocalDate.now().toString())
                .endDate(LocalDate.now().plusDays(5).toString())
                .build();
        TripEditRequest request = new TripEditRequest(dayGeneratorRequest, "Updated Trip Name");
        String requestBody = objectMapper.writeValueAsString(request);
        doNothing().when(tripFacade).updateTrip(request, userDetails);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .patch("/trip")
                        .principal(createAuthentication(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void deleteTrip_ShouldReturnNoContent() throws Exception {
        // Given
        Long tripId = 1L;
        String password = "password";
        TripRequest request = new TripRequest(tripId, password);
        String requestBody = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc
                .perform(MockMvcRequestBuilders
                        .delete("/trip")
                        .principal(createAuthentication(user))
                        .content(requestBody)
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

    private @NotNull Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(
                securityUser,
                user.getPassword(),
                securityUser.getAuthorities()
        );
    }
}

