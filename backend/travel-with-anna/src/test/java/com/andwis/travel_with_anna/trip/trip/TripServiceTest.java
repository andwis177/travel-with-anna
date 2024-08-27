package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.backpack.BackpackRepository;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.budget.BudgetRepository;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayRepository;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.trip.note.NoteRepository;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
@DisplayName("Trip Service tests")
class TripServiceTest {

    @Autowired
    private TripService tripService;
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

    private User user;
    private Role retrivedRole;
    private Long userId;
    private User secondaryUser;
    private Day day;
    private Backpack backpack;
    private Budget budget;
    private Note note;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        retrivedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userId = userRepository.save(user).getUserId();

        secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(2L)
                .build();
        secondaryUser.setEnabled(true);
        userRepository.save(secondaryUser);

        day = Day.builder()
                .date(LocalDate.now()).build();
        dayRepository.save(day);
        backpack = Backpack.builder()
                .item("item")
                .build();
        backpackRepository.save(backpack);
        budget = Budget.builder()
                .currency("USD")
                .toSpend(new BigDecimal("1000"))
                .build();
        budgetRepository.save(budget);
        note = Note.builder().build();
        noteRepository.save(note);

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
    void testSaveTrip() {
        //Given
        Trip trip = Trip.builder()
                .tripName("Saved trip")
                .viewers(new HashSet<>())
                .days(new ArrayList<>())
                .backpack(new HashSet<>())
                .build();

        trip.setOwner(user);
        trip.addViewer(secondaryUser);
        trip.addDay(day);
        trip.addBackpack(backpack);
        trip.setBudget(budget);
        trip.setNote(note);


        //When
        Long tripId = tripService.saveTrip(trip);
        Trip savedTrip = tripRepository.findById(tripId).orElse(null);

        //Then
        assertNotNull(savedTrip);
        assertEquals(tripId, savedTrip.getId());
        assertEquals(trip.getTripName(), savedTrip.getTripName());
        assertEquals(trip.getOwner(), savedTrip.getOwner());
        assertEquals(trip.getViewers(), savedTrip.getViewers());
        assertEquals(trip.getDays(), savedTrip.getDays());
        assertEquals(trip.getBackpack(), savedTrip.getBackpack());
        assertEquals(trip.getBudget(), savedTrip.getBudget());
        assertEquals(trip.getNote(), savedTrip.getNote());



    }

}