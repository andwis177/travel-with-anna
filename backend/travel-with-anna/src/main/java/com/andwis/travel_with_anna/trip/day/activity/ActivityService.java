package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.*;
import com.andwis.travel_with_anna.api.country.City;
import com.andwis.travel_with_anna.api.country.Country;
import com.andwis.travel_with_anna.handler.exception.ActivityNotFoundException;
import com.andwis.travel_with_anna.handler.exception.TripNotFoundException;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayResponse;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseService;
import com.andwis.travel_with_anna.utility.MessageResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDate;
import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {

    private static final String ACTIVITY_NOT_FOUND_MSG = "Activity not found";
    private static final String TRIP_ID_REQUIRED_MSG = "Trip ID is required";
    private static final String DATE_TIME_REQUIRED_MSG = "Date and Time is required";

    private final ActivityRepository activityRepository;
    private final DayService dayService;
    private final AddressService addressService;
    private final ActivityAuthorizationService activityAuthorizationService;
    private final ExpanseService expanseService;
    private final EntityManager entityManager;

    public Activity getById(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(ACTIVITY_NOT_FOUND_MSG));
    }

    @Transactional
    public Activity createSingleActivity(ActivityRequest request, UserDetails connectedUser) {
        Activity activity = createActivityEntity(request, connectedUser);
        activityAuthorizationService.authorizeSingleActivity(activity, connectedUser);

        processAddress(request, activity);
        return activityRepository.save(activity);
    }

    private void processAddress(@NotNull ActivityRequest request, Activity activity) {
        if (request.getAddressRequest() != null) {
            Address address = AddressMapper.toAddress(request.getAddressRequest());
            addressService.save(address);
            address.addLinkedActivity(activity);
        }
    }

    @Transactional
    public Activity createActivityEntity(ActivityRequest request, UserDetails connectedUser) {
        validateRequest(request);
        Day day = fetchDayForRequest(request, connectedUser);
        Activity activity = ActivityMapper.toActivity(request);
        day.addActivity(activity);
        return activity;
    }

    private void validateRequest(@NotNull ActivityRequest request) {
        if (request.getTripId() == null) {
            throw new TripNotFoundException(TRIP_ID_REQUIRED_MSG);
        }
        if (request.getDateTime() == null) {
            throw new DateTimeException(DATE_TIME_REQUIRED_MSG);
        }
    }

    private Day fetchDayForRequest(@NotNull ActivityRequest request, UserDetails connectedUser) {
        LocalDate requestedDateTime = toLocalDateTime(request.getDateTime()).toLocalDate();
        return dayService.getByTripIdAndDate(request.getTripId(), requestedDateTime, connectedUser);
    }

    @Transactional
    public void createAssociatedActivities(@NotNull ActivityAssociatedRequest request, UserDetails connectedUser) {
        Activity firstActivity = createActivityEntity(request.getFirstRequest(), connectedUser);
        Activity secondActivity = createActivityEntity(request.getSecondRequest(), connectedUser);

        activityRepository.saveAll(List.of(firstActivity, secondActivity));
        entityManager.flush();

        Address address;
        if (request.isAddressSeparated()) {
            setActivityAddress(request.getFirstRequest(), firstActivity);
            setActivityAddress(request.getSecondRequest(), secondActivity);
        } else {
            address = setActivityAddress(request.getFirstRequest(), firstActivity); // Use the same address for both
            secondActivity.addAddress(address);
        }

        firstActivity.setAssociatedId(secondActivity.getActivityId());
        secondActivity.setAssociatedId(firstActivity.getActivityId());
    }

    private Address setActivityAddress(@NotNull ActivityRequest request, Activity activity) {
        Address address = Address.builder().build();
        if (request.getAddressRequest() != null) {
            address = AddressMapper.toAddress(request.getAddressRequest());
            activity.setAddress(address);
        }
        return address;
    }

    @Transactional
    public MessageResponse updateActivity(@NotNull ActivityUpdateRequest request, UserDetails connectedUser) {
        Activity activity = getById(request.getActivityId());
        activityAuthorizationService.authorizeSingleActivity(activity, connectedUser);
        ActivityMapper.updateActivity(activity, request);
        updateActivityExpanseCategoryDescription(activity);
        String message;
        if (!toLocalDate(request.getOldActivityDate()).equals(toLocalDate(request.getNewActivityDate()))) {
            updateActivityDate(activity, request.getNewActivityDate(), connectedUser);
            updateActivityExpanseDate(activity, toLocalDate(request.getNewActivityDate()));
            message = "Activity date updated";
        } else
        if (activity.getAssociatedId() != null) {
            updateAssociatedActivity(activity, connectedUser);
            message = "Associated activity updated";
        } else {
            activityRepository.save(activity);
            message = "Activity updated";
        }
        return new MessageResponse(message);
    }

    @Transactional
    protected void updateActivityDate(@NotNull Activity activity, String newDate, UserDetails connectedUser) {
        Day oldDay = activity.getDay();
        Long tripId = activity.getDay().getTrip().getTripId();
        LocalDate newActivityDate = toLocalDate(newDate);
        Day newDay = dayService.getByTripIdAndDate(tripId, newActivityDate, connectedUser);
        oldDay.removeActivities(Set.of(activity));
        newDay.addActivity(activity);
        dayService.saveAllDays(Set.of(oldDay, newDay));
    }

    @Transactional
    protected void updateAssociatedActivity(@NotNull Activity activity, UserDetails connectedUser) {
        try {
            Activity associatedActivity = getById(activity.getAssociatedId());
            activityAuthorizationService.authorizeSingleActivity(associatedActivity, connectedUser);
            associatedActivity.setType(activity.getType());
            updateActivityExpanseCategoryDescription(activity);
            activityRepository.saveAll(List.of(activity, associatedActivity));
        } catch (ActivityNotFoundException e) {
            activityRepository.save(activity);
            log.error("Associated activity not found");
        }
    }

    public List<Activity> getActivitiesByDayId(Long dayId, UserDetails connectedUser) {
        List<Activity> activities = activityRepository.findByDayDayIdOrderByBeginTimeAsc(dayId);
        activityAuthorizationService.authorizeActivities(new HashSet<>(activities), connectedUser);
        return activities;
    }

    public ActivityDetailedResponse fetchActivitiesByDayId(Long dayId, UserDetails connectedUser) {
        List<Activity> activities = getActivitiesByDayId(dayId, connectedUser);
        List<ActivityResponse> activityResponses = ActivityMapper.toActivityResponseList(activities);
        AddressDetail addressDetail = buildAddressDetail(
                getActivitiesCountriesFromDay(activityResponses),
                getActivitiesCitiesFromDay(activityResponses)
        );

        return new ActivityDetailedResponse(
                addressDetail,
                activityResponses,
                getTotalAmount(activityResponses, ExpanseResponse::getPriceInTripCurrency),
                getTotalAmount(activityResponses, ExpanseResponse::getPaidInTripCurrency)
        );
    }

    private BigDecimal getTotalAmount(
            @NotNull List<ActivityResponse> activities,
            @NotNull Function<ExpanseResponse, BigDecimal> getAmountFunction) {
        return activities.stream()
                .map(ActivityResponse::getExpanse)
                .filter(Objects::nonNull)
                .map(getAmountFunction)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private @NotNull List<Country> getActivitiesCountriesFromDay(
            @NotNull List<ActivityResponse> activityResponses) {
        List<Country> countries = new ArrayList<>();
        activityResponses.stream()
                .map(ActivityResponse::getAddress)
                .filter(Objects::nonNull)
                .map(this::buildCountry)
                .filter(this::countryValidation)
                .forEach(countries::add);
        return countries;
    }

    private Country buildCountry(@NotNull AddressResponse addressResponse) {
        return Country.builder()
                .name(addressResponse.country())
                .currency(addressResponse.currency())
                .iso2(addressResponse.countryCode())
                .iso3(addressResponse.countryCode())
                .build();
    }

    private boolean countryValidation(@NotNull Country country) {
        return country.getName() != null && !country.getName().isEmpty();
    }

    private @NotNull List<City> getActivitiesCitiesFromDay(@NotNull List<ActivityResponse> activityResponses) {
        List<City> cities = new ArrayList<>();
        activityResponses.stream()
                .map(activity -> activity.getAddress().city())
                .filter(Objects::nonNull)
                .filter(city -> !city.isBlank())
                .map(city -> City.builder().name(city).build())
                .forEach(cities::add);
        return cities;
    }


    public AddressDetail buildAddressDetail(@NotNull List<Country> countries, @NotNull List<City> cities) {
        List<Country> filteredCountries = filterUniqueByName(countries, Country::getName);
        List<City> filteredCities = filterUniqueByName(cities, City::getName);

        return new AddressDetail(
                filteredCountries,
                getLastElement(countries),
                filteredCities,
                getLastElement(cities)
        );
    }

    private <T> List<T> filterUniqueByName(@NotNull List<T> items, Function<T, String> getNameFunction) {
        return items.stream()
                .collect(Collectors.toMap(
                        getNameFunction,
                        item -> item,
                        (existing, e) -> existing,
                        LinkedHashMap::new
                ))
                .values().stream()
                .toList();
    }

    private <T> @Nullable T getLastElement(@NotNull List<T> items) {
        int listSize = items.size();
        return items.isEmpty() ? null : items.get(listSize - 1);
    }

    public AddressDetail fetchAddressDetailByDayId(Long dayId, UserDetails connectedUser) {
        List<Activity> activities = getActivitiesByDayId(dayId, connectedUser);
        List<ActivityResponse> activityResponses = ActivityMapper.toActivityResponseList(activities);
        return buildAddressDetail(
                getActivitiesCountriesFromDay(activityResponses),
                getActivitiesCitiesFromDay(activityResponses)
        );
    }

    public AddressDetail fetchAddressDetailByTripId(Long tripId, UserDetails connectedUser) {
        List<DayResponse> days = dayService.getDays(tripId, connectedUser);
        List<ActivityResponse> activityResponses = new ArrayList<>();
        for (DayResponse day : days) {
            List<Activity> activities = getActivitiesByDayId(day.dayId(), connectedUser);
            activityResponses.addAll(ActivityMapper.toActivityResponseList(activities));
        }
        return buildAddressDetail(
                getActivitiesCountriesFromDay(activityResponses),
                getActivitiesCitiesFromDay(activityResponses)
        );
    }

    @Transactional
    public void deleteActivityById(@NotNull Long activityId, UserDetails connectedUser) {
        Activity activity = getById(activityId);
        activityAuthorizationService.authorizeSingleActivity(activity, connectedUser);
        deleteActivities(Set.of(activity));
    }

    @Transactional
    protected void deleteActivities(@NotNull Set<Activity> activities) {
        Set<Long> activityIds = collectActivityIds(activities);
        Set<Long> associatedIds = collectAssociatedIds(activities);
        activityIds.addAll(associatedIds);

        Set<Activity> allActivities = activityRepository.findAllByActivityIdIn(activityIds);
        Set<Long> allAddressIds = collectAddressIds(allActivities);
        activityRepository.deleteAll(allActivities);
        addressService.deleteExistingAddressesByIds(allAddressIds);
    }

    private Set<Long> collectActivityIds(@NotNull Set<Activity> activities) {
        return  activities.stream()
                .map(Activity::getActivityId)
                .collect(Collectors.toSet());
    }

    private Set<Long> collectAssociatedIds(@NotNull Set<Activity> activities) {
        return activities.stream()
                .map(Activity::getAssociatedId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }

    private Set<Long> collectAddressIds(@NotNull Set<Activity> allActivities) {
        return allActivities.stream()
                .filter(activity -> activity.getAddress() != null)
                .map(activity -> activity.getAddress().getAddressId())
                .collect(Collectors.toSet());
    }

    @Transactional
    public void deleteDayActivities(@NotNull Day day) {
        Set<Activity> activities = day.getActivities();
        if (activities != null) {
            deleteActivities(activities);
        }
    }

    private @NotNull StringBuilder createExpanseCategoryDescription(@NotNull Activity activity) {
        StringBuilder description = new StringBuilder();
        description
                .append(activity.getBadge().toUpperCase())
                .append(": ").append(activity.getType())
                .append("\n")
                .append(activity.getAddress().getPlace())
                .append("\n[")
                .append(activity.getAddress().getCity())
                .append("]");
        return description;
    }

    private void updateActivityExpanseCategoryDescription(@NotNull Activity activity) {
        if(activity.getExpanse() == null) {
            return;
        }
        StringBuilder description = createExpanseCategoryDescription(activity);
        expanseService.updateExpanseCategory(activity.getExpanse(), description.toString());
    }

    private void updateActivityExpanseDate(@NotNull Activity activity, LocalDate date) {
        if(activity.getExpanse() == null) {
            return;
        }
        expanseService.updateExpanseDate(activity.getExpanse(), date );
    }
}
