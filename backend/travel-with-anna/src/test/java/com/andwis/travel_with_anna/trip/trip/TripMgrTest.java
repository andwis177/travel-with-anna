package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.address.AddressService;
import com.andwis.travel_with_anna.handler.exception.TripNotFoundException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayGeneratorRequest;
import com.andwis.travel_with_anna.trip.day.DayRepository;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.user.*;
import com.andwis.travel_with_anna.utility.PageResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Transactional
@DisplayName("Trip Mgr tests")
class TripMgrTest {
    @Autowired
    private TripService tripService;
    @Autowired
    private UserService userService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private TripMgr tripMgr;
    @Autowired
    private UserAuthenticationService userAuthenticationService;
    @Mock
    private AddressService addressService;
    @Mock
    private DayService dayService;

    private UserDetails userDetails;
    private Trip testTrip;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthenticationManager authenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
        }
    }

    @Autowired
    private AuthenticationManager authenticationManager;

    @BeforeEach
    @Transactional
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = roleRepository.findByRoleName(USER.getRoleName()).orElse(
                roleRepository.save(Role.builder()
                        .roleName(USER.getRoleName())
                        .roleAuthority(USER.getAuthority())
                        .build()));


        User testUser = User.builder()
                .userName("testUser")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        userService.saveUser(testUser);

        Budget budget = Budget.builder()
                .currency("USD")
                .budgetAmount(BigDecimal.valueOf(1000))
                .build();

        Day day = Day.builder()
                .date(LocalDate.now())
                .activities(new HashSet<>())
                .build();
        dayRepository.save(day);

        testTrip = Trip.builder()
                .tripName("Initial Trip")
                .days(new HashSet<>())
                .build();
        testTrip.addBackpack(new Backpack());
        testTrip.addBudget(budget);
        testTrip.addDay(day);
        tripService.saveTrip(testTrip);
        testUser.addTrip(testTrip);
        userService.saveUser(testUser);

        userDetails = createUserDetails(testUser);
    }

    @Test
    void testCreateTrip() {
        // Given
        TripCreatorRequest request = new TripCreatorRequest(
                "New Trip", "USD", BigDecimal.valueOf(1000));

        // When
        Long tripId = tripMgr.createTrip(request, userDetails);
        Trip createdTrip = tripService.getTripById(tripId);

        // Then
        assertNotNull(createdTrip);
        assertEquals("New Trip", createdTrip.getTripName());
        assertNotNull(createdTrip.getBackpack());
        assertNotNull(createdTrip.getBudget());
    }

    @Test
    void testGetAllOwnersTrips() {
        // Given
        // When
        PageResponse<TripResponse> pageResponse = tripMgr.getAllOwnersTrips(0, 10, userDetails);

        // Then
        assertNotNull(pageResponse);
        assertEquals(1, pageResponse.getTotalElements());
        assertEquals("Initial Trip", pageResponse.getContent().getFirst().tripName());
    }

    @Test
    void testGetTripById() {
        // Given
        // When
        TripResponse tripRequest = tripMgr.getTripById(testTrip.getTripId(), userDetails);

        // Then
        assertNotNull(tripRequest);
        assertEquals("Initial Trip", tripRequest.tripName());
    }

    @Test
    @Transactional
    void testDeleteTrip() throws WrongPasswordException {
        // Given
        TripRequest request = new TripRequest(testTrip.getTripId(), "password");

        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken("testUser", "password");
        Authentication authentication = new UsernamePasswordAuthenticationToken("testUser", "password");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);

        doNothing().when(addressService).deleteExistingAddressesByIds(anySet());

        // When
        tripMgr.deleteTrip(request, userDetails);

        // Then
        assertThrows(TripNotFoundException.class, () -> tripService.getTripById(request.tripId()));
        User updatedUser = userAuthenticationService.retriveConnectedUser(userDetails);
        assertFalse(updatedUser.getTrips().stream().anyMatch(trip -> trip.getTripId().equals(request.tripId())));
    }

    @Test
    void testDeleteTripWithWrongPassword_ShouldThrowException() {
        // Given
        TripRequest request = new TripRequest(testTrip.getTripId(), "wrong_password");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Wrong password"));

        // When & Then
        assertThrows(WrongPasswordException.class, () -> tripMgr.deleteTrip(request, userDetails));
    }

    @Test
    @Transactional
    void testUpdateTrip() {
        // Given
        String newTripName = "Updated Trip Name";
        LocalDate newStartDate = LocalDate.now().plusDays(1);
        LocalDate newEndDate = LocalDate.now().plusDays(5);
        DayGeneratorRequest dayGeneratorRequest = DayGeneratorRequest.builder()
                .associatedTripId(testTrip.getTripId())
                .startDate(newStartDate.toString())
                .endDate(newEndDate.toString())
                .build();
        TripEditRequest tripEditRequest = TripEditRequest.builder()
                .tripName(newTripName)
                .dayGeneratorRequest(dayGeneratorRequest)
                .build();
        doNothing().when(dayService).changeTripDates(
                testTrip, newStartDate, newEndDate);

        // When
        tripMgr.updateTrip(tripEditRequest, userDetails);

        // Then
        Trip updatedTrip = tripService.getTripById(testTrip.getTripId());
        assertNotNull(updatedTrip);
        assertEquals(newTripName, updatedTrip.getTripName());
        assertEquals(newStartDate, updatedTrip.getDaysInOrder().getFirst().getDate());
        assertEquals(newEndDate, updatedTrip.getDaysInOrder().getLast().getDate());
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