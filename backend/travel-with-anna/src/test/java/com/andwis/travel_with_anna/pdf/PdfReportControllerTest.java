package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Pdf Report Controller Tests")
class PdfReportControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TripRepository tripRepository;
    private Long tripId;

    @BeforeEach
    void setup() {
        Address address = Address.builder()
                .address("Address")
                .city("City")
                .country("Country")
                .phone("Phone")
                .place("Place")
                .email("Email")
                .countryCode("PL")
                .website("Website")
                .currency("Currency")
                .build();

        Activity activity = Activity.builder()
                .activityTitle("Title")
                .beginTime(LocalTime.of(10, 10))
                .endTime(LocalTime.of(12, 10))
                .badge("Badge")
                .type("Type")
                .status("Status")
                .build();
        address.addActivity(activity);

        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrivedRole =  existingRole.orElseGet(() -> roleRepository.save(role));

        String encodedPassword = passwordEncoder.encode("password");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(retrivedRole)
                .avatarId(1L)
                .ownedTrips(new HashSet<>())
                .build();
        user.setEnabled(true);
        user = userRepository.save(user);

        Budget budget = Budget.builder()
                .currency("USD")
                .toSpend(BigDecimal.valueOf(1000))
                .build();

        Day day = Day.builder()
                .date(LocalDate.of(2023, 10, 10))
                .activities(new HashSet<>())
                .build();
        day.addActivity(activity);

        Trip trip = Trip.builder()
                .tripName("Initial Trip")
                .days(new HashSet<>())
                .owner(user)
                .build();
        trip.addBackpack(new Backpack());
        trip.addBudget(budget);
        trip.addDay(day);

        user.addTrip(trip);
        tripId = tripRepository.save(trip).getTripId();

        UserDetails userDetails = createUserDetails(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGeneratePdfReport_ShouldReturnPdfReport() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pdf/reports/trip/{tripId}", tripId)
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=report.pdf"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andExpect(result -> {
                    byte[] content = result.getResponse().getContentAsByteArray();
                    assertNotNull(content, "PDF content should not be null");
                    assertTrue(content.length > 0);
                });
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGeneratePdfReport_ShouldReturnNotFound_WhenInvalidTripId() throws Exception {
        // Given
        Long invalidTripId = -1L;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pdf/reports/trip/{tripId}", invalidTripId)
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGenerateExpansePdfReport_ShouldReturnPdfReport() throws Exception {
        // Given
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pdf/reports/expanse/{tripId}", tripId)
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=report.pdf"))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PDF))
                .andExpect(result -> {
                    byte[] content = result.getResponse().getContentAsByteArray();
                    assertNotNull(content, "PDF content should not be null");
                    assertTrue(content.length > 0);
                });
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "User")
    void testGenerateExpansePdfReport_ShouldReturnNotFound_WhenInvalidTripId() throws Exception {
        // Given
        Long invalidTripId = -1L;

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/pdf/reports/expanse/{tripId}", invalidTripId)
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isNotFound());
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