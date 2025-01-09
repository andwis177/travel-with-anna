package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.handler.exception.DayNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Day Service Tests")
class DayServiceTest {
    @Autowired
    private DayService dayService;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Trip testTrip;
    private Day day;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByRoleName(USER.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(1, USER.getRoleName(), USER.getAuthority())));

        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .enabled(true)
                .build();
        userRepository.save(user);

        day = Day.builder()
                .date(LocalDate.of(2023, 10, 10))
                .build();

        testTrip = Trip.builder()
                .tripName("Initial Trip")
                .days(new HashSet<>())
                .build();
        testTrip.addDay(day);
        tripRepository.save(testTrip);

        user.addTrip(testTrip);
        userRepository.save(user);

        userDetails = createUserDetails(user);
    }

    @AfterEach
    void tearDown() {
        dayRepository.deleteAll();
        tripRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testSaveDay() {
        // Given
        Day newDay = Day.builder().date(LocalDate.of(2023, 12, 10)).build();

        // When
        dayService.saveDay(newDay);

        // Then
        assertTrue(dayRepository.existsById(newDay.getDayId()));
    }

    @Test
    void testSaveAllDays() {
        // Given
        dayRepository.deleteAll();
        Day day1 = Day.builder().date(LocalDate.of(2023, 12, 11)).build();
        Day day2 = Day.builder().date(LocalDate.of(2023, 12, 12)).build();

        // When
        dayService.saveAllDays(Set.of(day1, day2));

        // Then
        assertEquals(2, dayRepository.findAll().size());
    }

    @Test
    @Transactional
    void testCreateDay() {
        //Given
        DayRequest dayRequest = DayRequest.builder()
                .date(LocalDate.of(2023, 11, 15))
                .trip(testTrip)
                .build();

        // When
        dayService.createDay(dayRequest);

        // Then
        assertTrue(testTrip.getDays().stream().anyMatch(d -> d.getDate().equals(dayRequest.getDate())));
    }

    @Test
    void testGetById_DayExists() {
        // Given
        // When
        Day foundDay = dayService.getById(day.getDayId());

        // Then
        assertEquals(day.getDayId(), foundDay.getDayId());
    }

    @Test
    void testGetById_DayNotFound() {
        //Given, When & Then
        assertThrows(DayNotFoundException.class, () -> dayService.getById(999L));
    }

    @Test
    void testGetDaysByTripId() {
        // Given
        // When
        Set<Day> days = dayService.getDaysByTripId(testTrip.getTripId(), userDetails);

        // Then
        assertEquals(1, days.size());
    }

    @Test
    void testGetByTripIdAndDate_DayExists() {
        // Given
        // When
        Day foundDay = dayService.getByTripIdAndDate(testTrip.getTripId(), day.getDate(), userDetails);

        // Then
        assertEquals(day.getDayId(), foundDay.getDayId());
    }

    @Test
    void testGetByTripIdAndDate_DayNotFound() {
        //Given, When & Then
        assertThrows(DayNotFoundException.class, () -> dayService.getByTripIdAndDate(testTrip.getTripId(), LocalDate.now(), userDetails));
    }

    @Test
    @Transactional
    void testGetDayById_ValidDay() {
        // Given
        Long dayId = day.getDayId();

        // When
        DayResponse response = dayService.getDayById(dayId, userDetails);

        // Then
        assertEquals(day.getDate(), response.date());
    }

    @Test
    void testGetDayById_InvalidDay() {
        // Given
        Long invalidDayId = 999L;

        // When & Then
        assertThrows(DayNotFoundException.class, () -> dayService.getDayById(invalidDayId, userDetails));
    }

    @Test
    @Transactional
    void testAddDay_FirstDay() {
        //Given
        // When
        dayService.addDay(testTrip.getTripId(), true, userDetails);

        // Then
        assertEquals(2, testTrip.getDays().size());
    }

    @Test
    @Transactional
    void testGenerateDays_ValidDates() {
        // Given
        DayGeneratorRequest generatorRequest = DayGeneratorRequest.builder()
                .associatedTripId(testTrip.getTripId())
                .startDate("2024-01-01")
                .endDate("2024-01-03")
                .build();

        // When
        dayService.generateDays(generatorRequest, userDetails);

        //Then
        assertEquals(3, dayRepository.findByTripTripIdOrderByDateAsc(testTrip.getTripId()).size());
    }

    @Test
    void testDeleteDay() {
        // Given
        Long dayId = day.getDayId();

        // When
        dayService.deleteDay(day, _ -> {});

        // Then
        assertFalse(dayRepository.existsById(dayId));
    }

    @Test
    @Transactional
    void testDeleteFirstOrLastDay_FirstDay() {
        // Given
        Consumer<Day> deleteFunction = day -> dayRepository.delete(day);

        // When
        dayService.deleteFirstOrLastDay(testTrip.getTripId(), true, deleteFunction, userDetails);

        // Then
        assertFalse(dayRepository.existsById(day.getDayId()));
    }

    @Test
    @Transactional
    void testDeleteFirstOrLastDay_LastDay() {
        // Given
        Day secondDay = Day.builder().date(LocalDate.of(2023, 10, 11)).build();
        Long secondDayId = dayRepository.save(secondDay).getDayId();
        testTrip.addDay(secondDay);
        tripRepository.save(testTrip);

        Consumer<Day> deleteFunction = day -> dayRepository.delete(day);

        // When
        dayService.deleteFirstOrLastDay(testTrip.getTripId(), false, deleteFunction, userDetails);

        // Then
        assertFalse(dayRepository.existsById(secondDayId));
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