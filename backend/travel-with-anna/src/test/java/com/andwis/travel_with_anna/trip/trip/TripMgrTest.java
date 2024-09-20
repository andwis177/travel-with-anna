package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.handler.exception.TripNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.budget.BudgetRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.utility.PageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

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
    private TripRepository tripRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private TripMgr tripMgr;

    private Trip testTrip;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrievedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        testUser = User.builder()
                .userName("testUser")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrievedRole)
                .avatarId(1L)
                .ownedTrips(new HashSet<>())
                .build();
        userService.saveUser(testUser);

        Budget budget = Budget.builder()
                .currency("USD")
                .toSpend(BigDecimal.valueOf(1000))
                .build();

        testTrip = Trip.builder()
                .tripName("Initial Trip")
                .build();
        testTrip.setBackpack(new Backpack());
        testTrip.setBudget(budget);
        tripService.saveTrip(testTrip);
        testUser.addTrip(testTrip);
        userService.saveUser(testUser);
    }

    @AfterEach
    void tearDown() {
        tripRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        budgetRepository.deleteAll();
    }

    @Test
    void testCreateTrip() {
        TripCreatorRequest request = new TripCreatorRequest("New Trip", "USD", BigDecimal.valueOf(1000));

        Long tripId = tripMgr.createTrip(request, createAuthentication(testUser));

        Trip createdTrip = tripService.getTripById(tripId);
        assertNotNull(createdTrip);
        assertEquals("New Trip", createdTrip.getTripName());
        assertNotNull(createdTrip.getBackpack());
        assertNotNull(createdTrip.getBudget());
    }

    @Test
    void testGetAllOwnersTrips() {

        PageResponse<TripResponse> pageResponse = tripMgr.getAllOwnersTrips(0, 10, createAuthentication(testUser));

        assertNotNull(pageResponse);
        assertEquals(1, pageResponse.getTotalElements());
        assertEquals("Initial Trip", pageResponse.getContent().getFirst().tripName());
    }

    @Test
    void testGetTripById() {
        TripResponse tripRequest = tripMgr.getTripById(testTrip.getTripId());

        assertNotNull(tripRequest);
        assertEquals("Initial Trip", tripRequest.tripName());
    }

    @Test
    void testChangeTripName() {
        String newName = "Updated Trip Name";
        Long updatedTripId = tripMgr.changeTripName(testTrip.getTripId(), newName);

        Trip updatedTrip = tripService.getTripById(updatedTripId);
        assertNotNull(updatedTrip);
        assertEquals(newName, updatedTrip.getTripName());
    }

    @Test
    void testDeleteTrip() {
        Long tripIdToDelete = testTrip.getTripId();

        tripMgr.deleteTrip(tripIdToDelete, createAuthentication(testUser));

        assertThrows(TripNotFoundException.class, () -> tripService.getTripById(tripIdToDelete));
        User updatedUser = userService.getConnectedUser(createAuthentication(testUser));
        assertFalse(updatedUser.getOwnedTrips().stream().anyMatch(trip -> trip.getTripId().equals(tripIdToDelete)));
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}