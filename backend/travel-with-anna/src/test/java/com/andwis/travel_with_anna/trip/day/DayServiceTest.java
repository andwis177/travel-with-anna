package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.address.AddressRepository;
import com.andwis.travel_with_anna.handler.exception.DayNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import com.andwis.travel_with_anna.trip.trip.TripService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Day Service Tests")
class DayServiceTest {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private TripService tripService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private DayService dayService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private Trip testTrip;
    private Day day;
    private UserDetails userDetails;
    private User user;

    @BeforeEach
    void setUp() {
        Activity activity = Activity.builder()
                .activityTitle("Title")
                .beginTime(LocalTime.of(10, 10))
                .endTime(LocalTime.of(12, 10))
                .badge("Badge")
                .type("Type")
                .status("Status")
                .build();

        Activity associatedActivity = Activity.builder()
                .activityTitle("Title associated")
                .beginTime(LocalTime.of(11, 11))
                .endTime(LocalTime.of(14, 14))
                .badge("Badge associated")
                .type("Type associated")
                .status("Status associated")
                .build();

        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrivedRole =  existingRole.orElseGet(() -> roleRepository.save(role));

        String encodedPassword = passwordEncoder.encode("password");
        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(retrivedRole)
                .avatarId(1L)
                .ownedTrips(new HashSet<>())
                .build();
        user.setEnabled(true);
        userRepository.save(user);

        Budget budget = Budget.builder()
                .currency("USD")
                .toSpend(BigDecimal.valueOf(1000))
                .build();

        day = Day.builder()
                .date(LocalDate.of(2023, 10, 10))
                .activities(new HashSet<>())
                .build();
        day.addActivity(activity);
        day.addActivity(associatedActivity);

        testTrip = Trip.builder()
                .tripName("Initial Trip")
                .days(new HashSet<>())
                .build();
        testTrip.addBackpack(new Backpack());
        testTrip.addBudget(budget);
        testTrip.addDay(day);
        user.addTrip(testTrip);
        tripRepository.save(testTrip);

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
        addressRepository.save(address);

        userDetails = createUserDetails(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testSaveDay() {
        // Given
        dayRepository.deleteAll();
        Day day = Day.builder()
                .date(LocalDate.of(2023, 10, 10))
                .build();

        // When
        dayService.saveDay(day);

        // Then
        List<Day> days = dayRepository.findAll();
        assertEquals(1, days.size());
    }

    @Test
    void testSaveAllDays() {
        // Given
        dayRepository.deleteAll();
        Day day = Day.builder()
                .date(LocalDate.of(2023, 10, 10))
                .build();

        Day secondDay = Day.builder()
                .date(LocalDate.of(2023, 10, 11))
                .build();

        Set<Day> days = Set.of(day, secondDay);

        // When
        dayService.saveAllDays(days);

        // Then
        List<Day> daysSaved = dayRepository.findAll();
        assertEquals(2, daysSaved.size());
    }

    @Test
    void testCreateDay() {
        // Given
        DayRequest dayRequest = DayRequest.builder()
                .date(LocalDate.of(2023, 11, 12))
                .trip(testTrip)
                .build();

        // When
        dayService.createDay(dayRequest);

        // Then
        Set<LocalDate> testTripDayDates = testTrip.getDays().stream().map(Day::getDate).collect(Collectors.toSet());
        assertEquals(2, testTrip.getDays().size());
        assertTrue(testTripDayDates.contains(LocalDate.of(2023, 11, 12)));
    }

    @Test
    void testGetById_DayExists() {
        // Given
        // When
        Day foundDay = dayService.getById(day.getDayId());

        // Then
        assertEquals(day.getDayId(), foundDay.getDayId());
        assertEquals(day.getDate(), foundDay.getDate());
    }

    @Test
    void testGetById_DayNotFound() {
        assertThrows(DayNotFoundException.class, () -> dayService.getById(101L));
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
        assertThrows(DayNotFoundException.class, () ->
                dayService.getByTripIdAndDate(testTrip.getTripId(), LocalDate.now(), userDetails));
    }

    @Test
    @Transactional
    void testGetDayById() {
        // Given
        // When
        DayResponse response = dayService.getDayById(day.getDayId(), userDetails);

        // Then
        assertNotNull(response);
        assertEquals(day.getDayId(), response.dayId());
    }

    @Test
    @Transactional
    void testAddDay_FirstDay() {
        // Given
        int numberOfDays = testTrip.getDays().size();
        DayAddDeleteRequest addDeleteRequest = DayAddDeleteRequest.builder()
                .tripId(testTrip.getTripId())
                .isFirst(true)
                .build();

        // When
        dayService.addDay(addDeleteRequest, userDetails);

        // Then
        assertEquals(numberOfDays + 1, testTrip.getDays().size());
    }

    @Test
    @Transactional
    void testGenerateDays_ValidDates() {
        // Given
        Budget secondBudget = Budget.builder()
                .currency("PLN")
                .toSpend(BigDecimal.valueOf(2000))
                .build();

        Trip trip = Trip.builder()
                .tripName("Generate Day Test Trip")
                .days(new HashSet<>())
                .build();
        trip.addBackpack(new Backpack());
        trip.addBudget(secondBudget);
        user.addTrip(trip);
        Long generateDayTripId = tripService.saveTrip(trip);

        DayGeneratorRequest generatorRequest = DayGeneratorRequest.builder()
                .tripId(generateDayTripId)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .build();

        // When
        dayService.generateDays(generatorRequest, userDetails);

        // Then
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(generateDayTripId);
        assertEquals(3, trip.getDays().size());
        assertEquals(LocalDate.of(2024, 1, 1), days.getFirst().getDate());
    }

    @Test
    @Transactional
    void testChangeTripDates() {
        // Given
        LocalDate startDate = LocalDate.of(2023, 10, 8);
        LocalDate endDate = LocalDate.of(2023, 10, 12);
        dayService.changeTripDates(testTrip, startDate, endDate);

        // When
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(testTrip.getTripId());

        // Then
        assertEquals(startDate, days.getFirst().getDate());
        assertEquals(endDate, days.getLast().getDate());
    }

    @Test
    void testDeleteDay() {
        // Given
        // When
        Long dayId = day.getDayId();
        dayService.deleteDay(day, activityService::deleteDayActivities);

        // Then
        assertFalse(dayRepository.existsById(dayId));
    }

    @Test
    @Transactional
    void testDeleteFirstOrLastDay_FirstDay() {
        // Given
        Budget secondBudget = Budget.builder()
                .currency("PLN")
                .toSpend(BigDecimal.valueOf(2000))
                .build();

        Trip trip = Trip.builder()
                .tripName("Generate Day Test Trip")
                .days(new HashSet<>())
                .build();
        trip.addBackpack(new Backpack());
        trip.addBudget(secondBudget);
        user.addTrip(trip);
        Long generateDayTripId = tripService.saveTrip(trip);

        DayGeneratorRequest generatorRequest = DayGeneratorRequest.builder()
                .tripId(generateDayTripId)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .build();
        dayService.generateDays(generatorRequest, userDetails);

        DayAddDeleteRequest requestForFirst = DayAddDeleteRequest.builder()
                .tripId(trip.getTripId())
                .isFirst(true)
                .build();

        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(trip.getTripId());
        Long firstDayId = days.getFirst().getDayId();

        // When
        dayService.deleteFirstOrLastDay(requestForFirst, activityService::deleteDayActivities, userDetails);

        // Then
        assertFalse(dayRepository.existsById(firstDayId));
    }

    @Test
    @Transactional
    void testDeleteFirstOrLastDay_LastDay() {
        // Given
        Budget secondBudget = Budget.builder()
                .currency("PLN")
                .toSpend(BigDecimal.valueOf(2000))
                .build();

        Trip trip = Trip.builder()
                .tripName("Generate Day Test Trip")
                .days(new HashSet<>())
                .build();
        trip.addBackpack(new Backpack());
        trip.addBudget(secondBudget);
        user.addTrip(trip);
        Long generateDayTripId = tripService.saveTrip(trip);

        DayGeneratorRequest generatorRequest = DayGeneratorRequest.builder()
                .tripId(generateDayTripId)
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 1, 3))
                .build();
        dayService.generateDays(generatorRequest, userDetails);

        DayAddDeleteRequest requestForLast = DayAddDeleteRequest.builder()
                .tripId(trip.getTripId())
                .isFirst(false)
                .build();

        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(trip.getTripId());
        Long lastDayId = days.getLast().getDayId();

        // When
        dayService.deleteFirstOrLastDay(requestForLast, activityService::deleteDayActivities, userDetails);

        // Then
        assertFalse(dayRepository.existsById(lastDayId));
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