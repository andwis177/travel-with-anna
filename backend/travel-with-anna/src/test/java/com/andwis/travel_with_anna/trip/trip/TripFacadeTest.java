package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.BackpackRepository;
import com.andwis.travel_with_anna.trip.budget.BudgetRepository;
import com.andwis.travel_with_anna.trip.day.DayRepository;
import com.andwis.travel_with_anna.trip.note.NoteRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.user.avatar.AvatarRepository;
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
import java.util.List;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Trip Facade tests")
class TripFacadeTest {

    @Autowired
    private TripFacade tripFacade;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private BackpackRepository backpackRepository;
    @Autowired
    private BudgetRepository budgetRepository;
    @Autowired
    private NoteRepository noteRepository;
    @Autowired
    private AvatarRepository avatarRepository;

    private User user;
    private User secondaryUser;


    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrievedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrievedRole)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userRepository.save(user);

        Avatar avatar = Avatar.builder()
                .avatar(AvatarImg.DEFAULT.getImg())
                .build();

        Long avatarId = avatarRepository.save(avatar).getAvatarId();

        secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrievedRole)
                .avatarId(avatarId)
                .build();
        secondaryUser.setEnabled(true);
        userRepository.save(secondaryUser);
    }

    @AfterEach()
    void cleanUp() {
        dayRepository.deleteAll();
        backpackRepository.deleteAll();
        budgetRepository.deleteAll();
        noteRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testCreateTrip() {
        // Given
        TripCreatorRequest trip = new TripCreatorRequest("trip name", "USD", new BigDecimal("1000"));

        // When
        tripFacade.createTrip(trip, createAuthentication(user));
        List<Trip> trips = tripRepository.findAll();

        // Then
        assertEquals(1, trips.size());
        assertEquals(trip.tripName(), trips.get(0).getTripName());
        assertEquals(trip.currency(), trips.get(0).getBudget().getCurrency());
        assertEquals(trip.toSpend(), trips.get(0).getBudget().getToSpend());
        assertEquals(user, trips.get(0).getOwner());
    }

    @Test
    void testChangeTripName() {
        // Given
        TripCreatorRequest tripRequest = new TripCreatorRequest("Old Trip Name", "USD", new BigDecimal("1000"));
        tripFacade.createTrip(tripRequest, createAuthentication(user));
        Trip trip = tripRepository.findAll().get(0);

        // When
        tripFacade.changeTripName(trip.getId(), "New Trip Name");

        // Then
        assertEquals("New Trip Name", trip.getTripName());
    }

    @Test
    void testGetAllOwnersTrips() {
        // Given
        TripCreatorRequest tripRequest = new TripCreatorRequest("Old Trip Name", "USD", new BigDecimal("1000"));
        tripFacade.createTrip(tripRequest, createAuthentication(user));

        // When
        PageResponse<TripDto> trips = tripFacade.getAllOwnersTrips(0, 100, createAuthentication(user) );

        // Then
        assertEquals(1, trips.getContent().size());
        assertEquals("Old Trip Name", trips.getContent().get(0).getTripName());
    }

    @Test
    void testTripById() {
        // Given
        TripCreatorRequest tripRequest = new TripCreatorRequest("Trip Name", "USD", new BigDecimal("1000"));
        Long id = tripFacade.createTrip(tripRequest, createAuthentication(user));

        // When
        TripDto tripRetrieved = tripFacade.getTripById(id);

        // Then
        assertEquals(id, tripRetrieved.getTripId());
        assertEquals("Trip Name", tripRetrieved.getTripName());
    }

    @Test
    void testDeleteTrip() {
        // Given
        TripCreatorRequest tripRequest = new TripCreatorRequest("Trip to delete", "USD", new BigDecimal("500"));
        tripFacade.createTrip(tripRequest, createAuthentication(user));
        Trip trip = tripRepository.findAll().get(0);

        // When
        tripFacade.deleteTrip(trip.getId(), createAuthentication(user));

        // Then
        List<Trip> trips = tripRepository.findAll();
        assertTrue(trips.isEmpty());
    }

    @Test
    void testAddViewer() {
        // Given
        TripCreatorRequest tripRequest = new TripCreatorRequest("Trip with Viewer", "USD", new BigDecimal("1500"));
        tripFacade.createTrip(tripRequest, createAuthentication(user));
        Trip trip = tripRepository.findAll().get(0);
        trip.setViewers(new HashSet<>());

        // When
        tripFacade.addViewer(trip.getId(), secondaryUser.getUserId(), createAuthentication(user));

        // Then
        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();
        assertTrue(updatedTrip.getViewers().contains(secondaryUser));
    }

    @Test
    void testGetTripViewers() {
        // Given
        TripCreatorRequest tripRequest = new TripCreatorRequest("Trip with Viewer", "USD", new BigDecimal("1500"));
        Long id = tripFacade.createTrip(tripRequest, createAuthentication(user));
        Trip trip = tripRepository.findById(id).orElseThrow();
        trip.setViewers(new HashSet<>());
        tripFacade.addViewer(trip.getId(), secondaryUser.getUserId(), createAuthentication(user));

        // When
        List<ViewerDto> viewers = tripFacade.getTripViewers(trip.getId());

        // Then
        assertNotNull(viewers);
        assertEquals(1, viewers.size());
        assertEquals(secondaryUser.getUserId(), viewers.get(0).getViewerId());
    }

    @Test
    void testRemoveViewer() {
        // Given
        TripCreatorRequest tripRequest = new TripCreatorRequest("Trip with Viewer", "USD", new BigDecimal("1500"));
        tripFacade.createTrip(tripRequest, createAuthentication(user));
        Trip trip = tripRepository.findAll().get(0);
        trip.setViewers(new HashSet<>());
        tripFacade.addViewer(trip.getId(), secondaryUser.getUserId(), createAuthentication(user));

        // When
        tripFacade.removeViewer(trip.getId(), secondaryUser.getUserId());

        // Then
        Trip updatedTrip = tripRepository.findById(trip.getId()).orElseThrow();
        assertFalse(updatedTrip.getViewers().contains(secondaryUser));
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}