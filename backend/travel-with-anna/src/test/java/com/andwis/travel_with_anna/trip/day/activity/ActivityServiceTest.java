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
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import com.andwis.travel_with_anna.trip.trip.TripService;
import com.andwis.travel_with_anna.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
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
    private TripRepository tripRepository;
    @Autowired
    private TripService tripService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private ExpanseRepository expanseRepository;
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

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());

        Budget budget = Budget.builder()
                .currency("USD")
                .toSpend(BigDecimal.valueOf(1000))
                .build();

        day = Day.builder()
                .date(LocalDate.of(2023, 10, 10))
                .activities(new HashSet<>()).build();

        testTrip = Trip.builder()
                .tripName("Initial Trip")
                .days(new HashSet<>())
                .build();
        testTrip.addBackpack(new Backpack());
        testTrip.addBudget(budget);
        testTrip.addDay(day);
        tripId = tripService.saveTrip(testTrip);

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

        day.addActivity(activity);
        day.addActivity(associatedActivity);
        activityId = activityRepository.save(activity).getActivityId();
        associatedActivityId = activityRepository.save(associatedActivity).getActivityId();

        address = Address.builder()
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

        addressRequest = AddressRequest.builder()
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

        activityRequest = ActivityRequest.builder()
                .tripId(tripId)
                .dateTime("2023-10-10T10:10")
                .endTime("12:10")
                .activityTitle("Title")
                .badge("Badge")
                .type("Type")
                .status("Status")
                .isDayTag(true)
                .build();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        activityRepository.deleteAll();
        dayRepository.deleteAll();
        addressRepository.deleteAll();
        tripRepository.deleteAll();
        expanseRepository.deleteAll();
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
        assertThrows(ActivityNotFoundException.class, () -> activityService.getById(2L));
    }

    @Test
    void testCreateSingleActivity_WithAddress() {
        // Given
        activityRequest.setAddressRequest(addressRequest);

        // When
        Long activityId = activityService.createSingleActivity(activityRequest);

        // Then
        Activity savedActivity = activityService.getById(activityId);
        assertEquals(activityRequest.getActivityTitle(), savedActivity.getActivityTitle());
        assertEquals(activityRequest.getAddressRequest().getAddress(), savedActivity.getAddress().getAddress());
    }

    @Test
    void testCreateSingleActivity_WithoutAddress() {
        // Given
        // When
        Long activityId = activityService.createSingleActivity(activityRequest);

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
                .isDayTag(true)
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
                .isDayTag(true)
                .build();

        AddressRequest secondAddressRequest = AddressRequest.builder()
                .address("Address2")
                .build();

        secondRequest.setAddressRequest(secondAddressRequest);

        ActivityAssociatedRequest request = ActivityAssociatedRequest.builder()
                .firstRequest(firstRequest)
                .secondRequest(secondRequest)
                .isAddressSeparated(true)
                .build();

        // When
        activityService.createAssociatedActivities(request);
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
                .isDayTag(true)
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
                .isDayTag(true)
                .build();

        ActivityAssociatedRequest request = ActivityAssociatedRequest.builder()
                .firstRequest(firstRequest)
                .secondRequest(secondRequest)
                .isAddressSeparated(false)
                .build();

        // When
        activityService.createAssociatedActivities(request);
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

        ActivityUpdateRequest request = ActivityUpdateRequest.builder()
                .activityId(activityId)
                .oldDate("2023-10-10")
                .newDate("2023-12-12")
                .startTime("10:10")
                .endTime("12:10")
                .build();

        // When
        String result = activityService.updateActivity(request);

        // Then
        Activity updatedActivity = activityService.getById(activityId);
        assertEquals("Activity date updated", result);
        assertEquals(LocalDate.of(2023, 12, 12), updatedActivity.getDay().getDate());
    }

    @Test
    void testUpdateActivity_UpdateAssociated() {
        // Given
        Day newDay = Day.builder()
                .date(LocalDate.of(2023, 12, 12))
                .activities(new HashSet<>())
                .build();
        testTrip.addDay(newDay);
        dayRepository.save(newDay);
        activity.setAssociatedId(associatedActivityId);
        activityRepository.save(activity);

        ActivityUpdateRequest request = ActivityUpdateRequest.builder()
                .activityId(activityId)
                .oldDate("2023-10-10")
                .newDate("2023-10-10")
                .startTime("10:10")
                .endTime("12:10")
                .type("New Type")
                .build();

        // When
        String result = activityService.updateActivity(request);

        // Then
        Activity updatedActivity = activityService.getById(activityId);
        assertEquals("Associated activity updated", result);
        assertEquals("New Type", updatedActivity.getType());
    }

    @Test
    void testGetActivitiesByDayId() {
        // Given
        // When
        Set<Activity> activities = activityService.getActivitiesByDayId(day.getDayId());

        // Then
        assertEquals(2, activities.size());
    }

    @Test
    @Transactional
    void testFetchActivitiesByDayId() {
        // Given
        Expanse expanse = Expanse.builder()
                .price(BigDecimal.valueOf(200))
                .paid(BigDecimal.valueOf(100))
                .currency("PLN")
                .exchangeRate(BigDecimal.valueOf(2.0))
                .build();

        testTrip.addExpanse(expanse);
        expanseRepository.save(expanse);

        activity.addAddress(address);
        activity.addExpanse(expanse);
        activityRepository.save(activity);

        Expanse expanseAssociated = Expanse.builder()
                .price(BigDecimal.valueOf(60))
                .paid(BigDecimal.valueOf(50))
                .currency("USD")
                .exchangeRate(BigDecimal.valueOf(1.0))
                .build();

        testTrip.addExpanse(expanseAssociated);
        expanseRepository.save(expanseAssociated);

        associatedActivity.addAddress(address);
        associatedActivity.addExpanse(expanseAssociated);
        activityRepository.save(associatedActivity);

        // When
        ActivityDetailedResponse response = activityService.fetchActivitiesByDayId(day.getDayId());
        System.out.println(response);
        // Then
        assertEquals(1, response.addressDetail().countries().size());
        assertEquals("Country", response.addressDetail().countries().getFirst().getName());
        assertEquals("Currency", response.addressDetail().countries().getFirst().getCurrency());
        assertEquals("PL", response.addressDetail().countries().getFirst().getIso2());
        assertEquals("Country", response.addressDetail().lastCountry().getName());
        assertEquals("Currency", response.addressDetail().lastCountry().getCurrency());
        assertEquals("PL", response.addressDetail().lastCountry().getIso2());
        assertEquals(1, response.addressDetail().cities().size());
        assertEquals("City", response.addressDetail().cities().getFirst().getCity());
        assertEquals("City", response.addressDetail().lastCity().getCity());
        assertEquals(2, response.activities().size());
        assertEquals("Title", response.activities().getFirst().getActivityTitle());
        assertEquals("Title associated", response.activities().getLast().getActivityTitle());
        assertEquals(0, response.totalPrice().compareTo(BigDecimal.valueOf(460)));
        assertEquals(0, response.totalPayment().compareTo(BigDecimal.valueOf(250)));
    }

    @Test
    void testFetchAddressDetailsByDayId() {
        // Given
        activity.addAddress(address);
        activityRepository.save(activity);

        associatedActivity.addAddress(address);
        activityRepository.save(associatedActivity);

        // When
        AddressDetail response = activityService.fetchAddressDetailByDayId(day.getDayId());

        // Then
        assertEquals(1, response.countries().size());
        assertEquals("Country", response.countries().getFirst().getName());
        assertEquals("Currency", response.countries().getFirst().getCurrency());
        assertEquals("PL", response.countries().getFirst().getIso2());
        assertEquals(1, response.cities().size());
        assertEquals("City", response.cities().getFirst().getCity());
    }

    @Test
    @Transactional
    void testFetchAddressDetailByTripId() {
        // Given
        activity.addAddress(address);
        activityRepository.save(activity);

        associatedActivity.addAddress(address);
        activityRepository.save(associatedActivity);

        // When
        AddressDetail response = activityService.fetchAddressDetailByTripId(tripId);

        // Then
        assertEquals(1, response.countries().size());
        assertEquals("Country", response.countries().getFirst().getName());
        assertEquals("Currency", response.countries().getFirst().getCurrency());
        assertEquals("PL", response.countries().getFirst().getIso2());
        assertEquals(1, response.cities().size());
        assertEquals("City", response.cities().getFirst().getCity());
    }

    @Test
    void testDeleteActivityById() {
        // Given
        activity.setAssociatedId(associatedActivityId);
        activityRepository.save(activity);

        // When
        activityService.deleteActivityById(activityId);

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
}