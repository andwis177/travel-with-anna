package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Trip Service tests")
class TripServiceTest {
    @Autowired
    private TripService tripService;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = roleRepository.findByRoleName(USER.getRoleName())
                .orElseGet(() -> roleRepository.save(Role.builder()
                        .roleName(USER.getRoleName())
                        .roleAuthority(USER.getAuthority())
                        .build()));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Test
    void shouldSaveTrip() {
        // Given
        Trip trip = Trip.builder()
                .tripName("Trip")
                .owner(user)
                .build();

        // When
        Long savedTripId = tripService.saveTrip(trip);
        Trip savedTrip = tripRepository.findById(savedTripId).orElse(null);

        // Then
        assertNotNull(savedTripId);
        assertNotNull(savedTrip);
        assertEquals("Trip", savedTrip.getTripName());

        Long ownerId = savedTrip.getOwner().getUserId();
        assertEquals(user.getUserId(), ownerId);
    }

    @Test
    void shouldGetAllTrips() {
        // Given
        Trip trip1 = Trip.builder()
                .tripName("Trip 1")
                .owner(user)
                .build();
        tripService.saveTrip(trip1);

        Trip trip2 = Trip.builder()
                .tripName("Trip 2")
                .owner(user)
                .build();
        tripService.saveTrip(trip2);

        Pageable pageable = PageRequest.of(0, 10);

        // When
        List<Trip> trips = tripService.getTripsByOwnerId(user.getUserId(), pageable).getContent();

        // Then
        assertEquals(2, trips.size());
        assertTrue(trips.stream().anyMatch(trip -> trip.getTripName().equals("Trip 1")));
        assertTrue(trips.stream().anyMatch(trip -> trip.getTripName().equals("Trip 2")));
    }

    @Test
    void shouldGetTripById() {
        // Given
        Trip trip = Trip.builder()
                .tripName("Trip")
                .owner(user)
                .build();

        // When
        Long savedTripId = tripService.saveTrip(trip);
        Trip savedTrip = tripRepository.findById(savedTripId).orElse(null);

        // Then
        assertNotNull(savedTripId);
        assertNotNull(savedTrip);
        assertEquals("Trip", savedTrip.getTripName());
        assertEquals(user.getUserName(), savedTrip.getOwner().getUserName());
        assertEquals(user.getEmail(), savedTrip.getOwner().getEmail());
    }

    @Transactional
    @Test
    void shouldDeleteTrip() {
        // Given
        Trip trip = Trip.builder()
                .tripName("Trip to Delete")
                .owner(user)
                .build();

        Long savedTripId = tripService.saveTrip(trip);
        Pageable pageable = PageRequest.of(0, 10);

        // When
        tripService.deleteById(savedTripId);
        List<Trip> trips = tripService.getTripsByOwnerId(user.getUserId(), pageable).getContent();

        // Then
        assertEquals(0, trips.size());
    }
}