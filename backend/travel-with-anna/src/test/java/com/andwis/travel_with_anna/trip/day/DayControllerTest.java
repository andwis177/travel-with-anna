package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
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

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Day Controller Tests")
class DayControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @MockBean
    private DayFacade facade;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());

        String encodedPassword = passwordEncoder.encode("password");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .ownedTrips(new HashSet<>())
                .build();
        user.setEnabled(true);

        userDetails = createUserDetails(user);
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void addDay_ShouldReturnAccepted() throws Exception {
        // Given
        DayAddDeleteRequest request = new DayAddDeleteRequest(1L, true);
        doNothing().when(facade).addDay(request, userDetails);
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/day/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getDayById_ShouldReturnDayResponse() throws Exception {
        // Given
        DayResponse dayResponse = new DayResponse(
                1L, LocalDate.now(), "Monday", true, 1, null, 1L, List.of());
        when(facade.getDayById(eq(1L), any())).thenReturn(dayResponse);

        // When & Then
        mockMvc.perform(get("/day/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(1))
                .andExpect(jsonPath("$.dayOfWeek").value("Monday"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getDays_ShouldReturnListOfDays() throws Exception {
        // Given
        DayResponse dayResponse = new DayResponse(
                1L, LocalDate.now(), "Monday", true, 1, null, 1L, List.of());
        when(facade.getDays(eq(1L), any())).thenReturn(List.of(dayResponse));

        // When & Then
        mockMvc.perform(get("/day/trip/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayId").value(1))
                .andExpect(jsonPath("$[0].dayOfWeek").value("Monday"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void generateDays_ShouldReturnAccepted() throws Exception {
        // Given
        DayGeneratorRequest request = new DayGeneratorRequest(
                1L, LocalDate.now(), LocalDate.now().plusDays(5));
        doNothing().when(facade).generateDays(request, userDetails);
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/day/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void deleteDay_ShouldReturnNoContent() throws Exception {
        // Given
        DayAddDeleteRequest request = new DayAddDeleteRequest(1L, true);
        doNothing().when(facade).deleteDay(request, userDetails);
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(delete("/day")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
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