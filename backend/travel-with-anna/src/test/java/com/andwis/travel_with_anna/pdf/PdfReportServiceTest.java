package com.andwis.travel_with_anna.pdf;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.handler.exception.TripNotFoundException;
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
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PdfReportServiceTest {
    @Autowired
    private PdfReportService pdfReportService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TripRepository tripRepository;
    private Long tripId;
    private UserDetails unauthorizedUserDetails;
    private UserDetails userDetails;

    @BeforeEach
    void setup() {
        Address address = Address.builder()
                .address("Address")
                .city("City")
                .country("Country")
                .phoneNumber("Phone")
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
        address.addLinkedActivity(activity);

        Role role = roleRepository.findByRoleName(USER.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(1, USER.getRoleName(), USER.getAuthority())));

        String encodedPassword = passwordEncoder.encode("password");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);
        user = userRepository.save(user);

        User unauthorizedUser = User.builder()
                .userName("unauthorizedUser")
                .email("emailUnauthorized@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        unauthorizedUser.setEnabled(true);
        unauthorizedUserDetails = createUserDetails(unauthorizedUser);

        Budget budget = Budget.builder()
                .currency("USD")
                .budgetAmount(BigDecimal.valueOf(1000))
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
        userDetails = createUserDetails(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @Transactional
    void shouldGenerateTripPdfReport() {
        // Given
        // When
        ByteArrayOutputStream pdfReport = pdfReportService.createTripPdfReport(tripId, userDetails);

        // Then
        assertNotNull(pdfReport);
        assertTrue(pdfReport.size() > 0);
    }

    @Test
    void shouldThrowUnauthorizedExceptionForInvalidUser() {
        // Given
        // When
        Exception exception = assertThrows(
                BadCredentialsException.class,
                () -> pdfReportService.createTripPdfReport(tripId, unauthorizedUserDetails)
        );

        // Then
        assertEquals("You are not authorized to access", exception.getMessage());
    }

    @Test
    void shouldThrowTripNotFoundExceptionForInvalidTripId() {
        // Given
        // When
        Exception exception = assertThrows(
                TripNotFoundException.class,
                () -> pdfReportService.createTripPdfReport(-1L, userDetails)
        );

        // Then
        assertEquals("Trip not found when generating expense PDF report", exception.getMessage());
    }

    @Test
    @Transactional
    void shouldGenerateExpansePdfReport() {
        // Given
        // When
        ByteArrayOutputStream pdfReport = pdfReportService.createExpansePdfReport(tripId, userDetails);

        // Then
        assertNotNull(pdfReport);
        assertTrue(pdfReport.size() > 0);
    }

    @Test
    void shouldThrowUnauthorizedExceptionForInvalidUser_ExpansePdfReport() {
        // Given
        // When
        Exception exception = assertThrows(
                BadCredentialsException.class,
                () -> pdfReportService.createExpansePdfReport(tripId, unauthorizedUserDetails)
        );

        // Then
        assertEquals("You are not authorized to access", exception.getMessage());
    }

    @Test
    void shouldThrowTripNotFoundExceptionForInvalidTripId_ExpansePdfReport() {
        // Given
        // When
        Exception exception = assertThrows(
                TripNotFoundException.class,
                () -> pdfReportService.createExpansePdfReport(-1L, userDetails)
        );

        // Then
        assertEquals("Trip not found when generating expense PDF report", exception.getMessage());
    }

    @Test
    void shouldHandlePdfCreationExceptionGracefully_ExpansePdfReport() {
        // Given
        // When
        Exception exception = assertThrows(
                TripNotFoundException.class,
                () -> pdfReportService.createExpansePdfReport(999L, userDetails)
        );

        // Then
        assertEquals("Trip not found when generating expense PDF report", exception.getMessage());
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