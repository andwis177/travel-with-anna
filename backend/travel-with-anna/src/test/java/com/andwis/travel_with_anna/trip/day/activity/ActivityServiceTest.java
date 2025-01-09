package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.address.AddressRepository;
import com.andwis.travel_with_anna.address.AddressRequest;
import com.andwis.travel_with_anna.handler.exception.ActivityNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayRepository;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseRepository;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.utility.MessageResponse;
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

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Activity Service Tests")
class ActivityServiceTest {
    @Autowired
    private ActivityService activityService;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private DayRepository dayRepository;
    @Autowired
    private AddressRepository addressRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TripService tripService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ExpanseRepository expanseRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private Trip testTrip;
    private Activity activity;
    private Activity associatedActivity;
    private Day day;
    private ActivityRequest activityRequest;
    private Address address;
    private AddressRequest addressRequest;
    private Long tripId;
    private Long activityId;
    private Long associatedActivityId;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        activity = Activity.builder()
                .activityTitle("Title")
                .beginTime(LocalTime.of(10, 10))
                .endTime(LocalTime.of(12, 10))
                .badge("Badge")
                .type("Type")
                .status("Status")
                .build();

        associatedActivity = Activity.builder()
                .activityTitle("Title associated")
                .beginTime(LocalTime.of(11, 11))
                .endTime(LocalTime.of(14, 14))
                .badge("Badge associated")
                .type("Type associated")
                .status("Status associated")
                .build();

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
        userRepository.save(user);

        Budget budget = Budget.builder()
                .currency("USD")
                .budgetAmount(BigDecimal.valueOf(1000))
                .build();

        day = Day.builder()
                .date(LocalDate.of(2023, 10, 10))
                .activities(new HashSet<>()).build();
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
        tripId = tripService.saveTrip(testTrip);

        day.addActivity(activity);
        day.addActivity(associatedActivity);
        activityId = activityRepository.save(activity).getActivityId();
        associatedActivityId = activityRepository.save(associatedActivity).getActivityId();

        address = Address.builder()
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

        addressRequest = AddressRequest.builder()
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

        activityRequest = ActivityRequest.builder()
                .tripId(tripId)
                .dateTime("2023-10-10T10:10")
                .endTime("12:10")
                .activityTitle("Title")
                .badge("Badge")
                .type("Type")
                .status("Status")
                .dayTag(true)
                .build();

        userDetails = createUserDetails(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testGetById_Found() {
        // Given
        // When
        Activity result = activityService.getById(activityId);

        // Then
        assertEquals(activity.getActivityId(), result.getActivityId());
        assertEquals(activity.getActivityTitle(), result.getActivityTitle());
    }

    @Test
    void testGetById_NotFound() {
        assertThrows(ActivityNotFoundException.class,
                () -> activityService.getById(2L));
    }

    @Test
    @Transactional
    void testCreateSingleActivity_WithAddress() {
        // Given
        activityRequest.setAddressRequest(addressRequest);

        // When
        Activity createdActivity = activityService.createSingleActivity(
                activityRequest, userDetails);

        // Then
        Address address = createdActivity.getAddress();
        assertEquals(activityRequest.getActivityTitle(), createdActivity.getActivityTitle());
        assertEquals(addressRequest.getAddress(), address.getAddress());
    }

    @Test
    void testCreateSingleActivity_WithoutAddress() {
        // Given
        // When
        activityService.createSingleActivity(activityRequest, userDetails);

        // Then
        Activity savedActivity = activityService.getById(activityId);
        assertEquals(activityRequest.getActivityTitle(), savedActivity.getActivityTitle());
        assertNull(savedActivity.getAddress());
    }

    @Test
    void testCreateAssociatedActivities_WithSeparateAddresses() {
        // Given
        activityRepository.deleteAll();
        ActivityRequest firstRequest = ActivityRequest.builder()
                .tripId(tripId)
                .dateTime("2023-10-10T10:10")
                .endTime("12:10")
                .activityTitle("Title1")
                .badge("Badge1")
                .type("Type1")
                .status("Status1")
                .dayTag(true)
                .build();

        AddressRequest firstAddressRequest = AddressRequest.builder()
                .address("Address1")
                .build();
        firstRequest.setAddressRequest(firstAddressRequest);

        ActivityRequest secondRequest = ActivityRequest.builder()
                .tripId(tripId)
                .dateTime("2023-10-10T11:10")
                .endTime("13:10")
                .activityTitle("Title2")
                .badge("Badge2")
                .type("Type2")
                .status("Status2")
                .dayTag(true)
                .build();

        AddressRequest secondAddressRequest = AddressRequest.builder()
                .address("Address2")
                .build();

        secondRequest.setAddressRequest(secondAddressRequest);

        ActivityAssociatedRequest request = ActivityAssociatedRequest.builder()
                .firstRequest(firstRequest)
                .secondRequest(secondRequest)
                .addressSeparated(true)
                .build();

        // When
        activityService.createAssociatedActivities(request, userDetails);
        List<Activity> activities = activityRepository.findAll();

        // Then
        assertEquals(2, activities.size());
        assertEquals(firstRequest.getActivityTitle(), activities.getFirst().getActivityTitle());
        assertEquals(firstAddressRequest.getAddress(), activities.getFirst().getAddress().getAddress());
        assertEquals(secondRequest.getActivityTitle(), activities.getLast().getActivityTitle());
        assertEquals(secondAddressRequest.getAddress(), activities.getLast().getAddress().getAddress());
    }

    @Test
    void testCreateAssociatedActivities_WithSharedAddress() {
        // Given
        activityRepository.deleteAll();
        ActivityRequest firstRequest = ActivityRequest.builder()
                .tripId(tripId)
                .dateTime("2023-10-10T10:10")
                .endTime("12:10")
                .activityTitle("Title1")
                .badge("Badge1")
                .type("Type1")
                .status("Status1")
                .dayTag(true)
                .build();

        AddressRequest firstAddressRequest = AddressRequest.builder()
                .address("Address1")
                .build();
        firstRequest.setAddressRequest(firstAddressRequest);

        ActivityRequest secondRequest = ActivityRequest.builder()
                .tripId(tripId)
                .dateTime("2023-10-10T11:10")
                .endTime("13:10")
                .activityTitle("Title2")
                .badge("Badge2")
                .type("Type2")
                .status("Status2")
                .dayTag(true)
                .build();

        ActivityAssociatedRequest request = ActivityAssociatedRequest.builder()
                .firstRequest(firstRequest)
                .secondRequest(secondRequest)
                .addressSeparated(false)
                .build();

        // When
        activityService.createAssociatedActivities(request, userDetails);
        List<Activity> activities = activityRepository.findAll();

        // Then
        assertEquals(2, activities.size());
        assertEquals(firstRequest.getActivityTitle(), activities.getFirst().getActivityTitle());
        assertEquals(firstAddressRequest.getAddress(), activities.getFirst().getAddress().getAddress());
        assertEquals(secondRequest.getActivityTitle(), activities.getLast().getActivityTitle());
        assertEquals(firstAddressRequest.getAddress(), activities.getLast().getAddress().getAddress());
    }

    @Test
    void testUpdateActivity_UpdateDate() {
        // Given
        Day newDay = Day.builder()
                .date(LocalDate.of(2023, 12, 12))
                .activities(new HashSet<>())
                .build();
        testTrip.addDay(newDay);
        dayRepository.save(newDay);

        Expanse expanse = Expanse.builder()
                .expanseName("Expanse")
                .date(LocalDate.of(2023, 10, 10))
                .price(BigDecimal.valueOf(200))
                .paid(BigDecimal.valueOf(100))
                .currency("PLN")
                .exchangeRate(BigDecimal.valueOf(2.0))
                .build();

        testTrip.addExpanse(expanse);
        activity.addExpanse(expanse);
        activity.addAddress(address);
        activityRepository.save(activity);

        ActivityUpdateRequest request = ActivityUpdateRequest.builder()
                .activityId(activityId)
                .oldActivityDate("2023-10-10")
                .newActivityDate("2023-12-12")
                .startTime("10:10")
                .endTime("12:10")
                .build();

        // When
        MessageResponse result = activityService.updateActivity(request, userDetails);

        // Then
        Activity updatedActivity = activityService.getById(activityId);
        assertEquals("Activity date updated", result.message());
        assertEquals(LocalDate.of(2023, 12, 12), updatedActivity.getDay().getDate());
        assertEquals("10:10", updatedActivity.getFormattedBeginTime());
        assertEquals("12:10", updatedActivity.getFormattedEndTime());
    }

    @Transactional
    @Test
    void testUpdateActivity_UpdateAssociated() {
        // Given
        Day newDay = Day.builder()
                .date(LocalDate.of(2023, 12, 12))
                .activities(new HashSet<>())
                .build();
        testTrip.addDay(newDay);
        dayRepository.save(newDay);

        Expanse expanse = Expanse.builder()
                .expanseName("Expanse")
                .date(LocalDate.of(2023, 10, 10))
                .price(BigDecimal.valueOf(200))
                .paid(BigDecimal.valueOf(100))
                .currency("PLN")
                .exchangeRate(BigDecimal.valueOf(2.0))
                .build();

        testTrip.addExpanse(expanse);
        activity.addExpanse(expanse);
        activity.addAddress(address);
        associatedActivity.addAddress(address);
        activity.setAssociatedId(associatedActivityId);
        activityRepository.save(activity);

        ActivityUpdateRequest request = ActivityUpdateRequest.builder()
                .activityId(activityId)
                .oldActivityDate("2023-10-10")
                .newActivityDate("2023-10-10")
                .startTime("10:10")
                .type("New Type")
                .addressRequest(addressRequest)
                .isDayTag(true)
                .build();

        String expanseDescription = "BADGE: New Type\nPlace\n[City]";

        // When
        MessageResponse result = activityService.updateActivity(request, userDetails);

        // Then
        Activity updatedActivity = activityService.getById(activityId);
        assertEquals("Associated activity updated", result.message());
        assertEquals("New Type", updatedActivity.getType());
        assertEquals(expanseDescription, updatedActivity.getExpanse().getExpanseCategory());
        assertEquals("10:10", updatedActivity.getFormattedBeginTime());
    }

    @Transactional
    @Test
    void testUpdateActivity_ExpanseCategoryDescription() {
        // Given
        Expanse expanse = Expanse.builder()
                .expanseName("Expanse")
                .expanseCategory("Old Category")
                .date(LocalDate.of(2023, 10, 10))
                .price(BigDecimal.valueOf(200))
                .paid(BigDecimal.valueOf(100))
                .currency("PLN")
                .exchangeRate(BigDecimal.valueOf(2.0))
                .build();
        testTrip.addExpanse(expanse);
        activity.addExpanse(expanse);
        activity.addAddress(address);
        activityRepository.save(activity);

        ActivityUpdateRequest request = ActivityUpdateRequest.builder()
                .activityId(activityId)
                .oldActivityDate("2023-10-10")
                .newActivityDate("2023-10-10")
                .startTime("10:10")
                .endTime("12:10")
                .type("New Type")
                .addressRequest(addressRequest)
                .isDayTag(true)
                .build();

        String expanseDescription = "BADGE: New Type\nPlace\n[City]";

        // When
        MessageResponse result = activityService.updateActivity(request, userDetails);

        // Then
        assertEquals("Activity updated", result.message());
        Activity updatedActivity = activityService.getById(activityId);
        assertEquals(expanseDescription, updatedActivity.getExpanse().getExpanseCategory());
    }


    @Test
    void testGetActivitiesByDayId() {
        // Given
        // When
        List<Activity> activities = activityService.getActivitiesByDayId(
                day.getDayId(), userDetails);

        // Then
        assertEquals(2, activities.size());
    }

    @Test
    @Transactional
    void testFetchActivitiesByDayId() {
        // Given
        Expanse firstexpanse = Expanse.builder()
                .price(BigDecimal.valueOf(200))
                .paid(BigDecimal.valueOf(100))
                .currency("PLN")
                .exchangeRate(BigDecimal.valueOf(2.0))
                .build();

        Expanse expanseAssociated = Expanse.builder()
                .price(BigDecimal.valueOf(60))
                .paid(BigDecimal.valueOf(50))
                .currency("USD")
                .exchangeRate(BigDecimal.valueOf(1.0))
                .build();

        testTrip.addExpanse(firstexpanse);
        testTrip.addExpanse(expanseAssociated);
        expanseRepository.save(expanseAssociated);
        expanseRepository.save(firstexpanse);

        addressRepository.save(address);
        activity.addAddress(address);

        activity.addExpanse(firstexpanse);
        associatedActivity.addAddress(address);
        associatedActivity.addExpanse(expanseAssociated);
        activityRepository.save(activity);
        activityRepository.save(associatedActivity);

        // When
        ActivityDetailedResponse response = activityService.fetchActivitiesByDayId(
                day.getDayId(), userDetails);

        // Then
        assertEquals(1, response.addressDetail().countries().size());
        assertEquals("Country", response.addressDetail().countries().getFirst().getName());
        assertEquals("Currency", response.addressDetail().countries().getFirst().getCurrency());
        assertEquals("PL", response.addressDetail().countries().getFirst().getIso2());
        assertEquals("Country", response.addressDetail().lastCountry().getName());
        assertEquals("Currency", response.addressDetail().lastCountry().getCurrency());
        assertEquals("PL", response.addressDetail().lastCountry().getIso2());
        assertEquals(1, response.addressDetail().cities().size());
        assertEquals("City", response.addressDetail().cities().getFirst().getName());
        assertEquals("City", response.addressDetail().lastCity().getName());
        assertEquals(2, response.activities().size());
        assertEquals("Title", response.activities().getFirst().getActivityTitle());
        assertEquals("Title associated", response.activities().getLast().getActivityTitle());
        assertEquals(0, response.totalPrice().compareTo(BigDecimal.valueOf(460)));
        assertEquals(0, response.totalPayment().compareTo(BigDecimal.valueOf(250)));
    }

    @Test
    void testFetchAddressDetailsByDayId() {
        // Given
        addressRepository.save(address);
        activity.addAddress(address);
        associatedActivity.addAddress(address);
        activityRepository.save(activity);
        activityRepository.save(associatedActivity);

        // When
        AddressDetail response = activityService.fetchAddressDetailByDayId(
                day.getDayId(), userDetails);

        // Then
        assertEquals(1, response.countries().size());
        assertEquals("Country", response.countries().getFirst().getName());
        assertEquals("Currency", response.countries().getFirst().getCurrency());
        assertEquals("PL", response.countries().getFirst().getIso2());
        assertEquals(1, response.cities().size());
        assertEquals("City", response.cities().getFirst().getName());
    }

    @Test
    @Transactional
    void testFetchAddressDetailByTripId() {
        // Given
        addressRepository.save(address);
        activity.addAddress(address);
        associatedActivity.addAddress(address);
        activityRepository.save(activity);
        activityRepository.save(associatedActivity);

        // When
        AddressDetail response = activityService.fetchAddressDetailByTripId(
                tripId, userDetails);

        // Then
        assertEquals(1, response.countries().size());
        assertEquals("Country", response.countries().getFirst().getName());
        assertEquals("Currency", response.countries().getFirst().getCurrency());
        assertEquals("PL", response.countries().getFirst().getIso2());
        assertEquals(1, response.cities().size());
        assertEquals("City", response.cities().getFirst().getName());
    }

    @Test
    void testDeleteActivityById() {
        // Given
        activity.setAssociatedId(associatedActivityId);
        activityRepository.save(activity);

        // When
        activityService.deleteActivityById(activityId, userDetails);

        // Then
        assertTrue(activityRepository.findAll().isEmpty());
    }

    @Test
    void testDeleteDayActivities() {
        // Given
        activity.setAssociatedId(associatedActivityId);
        activityRepository.save(activity);

        // When
        activityService.deleteDayActivities(day);

        // Then
        assertTrue(activityRepository.findAll().isEmpty());
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