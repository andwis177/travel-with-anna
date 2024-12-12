package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.address.AddressMapper;
import com.andwis.travel_with_anna.address.AddressService;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
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
    private final ActivityRepository activityRepository;
    private final DayService dayService;
    private final AddressService addressService;
    private final ActivityAuthorizationService activityAuthorizationService;
    private final ExpanseService expanseService;

    public Activity getById(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity not found"));
    }

    @Transactional
    public Activity createSingleActivity(ActivityRequest request, UserDetails connectedUser) {
        Address address;
        Activity activity = createActivity(request, connectedUser);
        activityAuthorizationService.verifyActivityOwner(activity, connectedUser);

        if (request.getAddressRequest() != null) {
            address = AddressMapper.toAddress(request.getAddressRequest());
            addressService.save(address);
            address.addActivity(activity);
        }
        return activityRepository.save(activity);
    }

    @Transactional
    public void createAssociatedActivities(@NotNull ActivityAssociatedRequest request, UserDetails connectedUser) {
        Activity firstActivity = createActivity(request.getFirstRequest(), connectedUser);
        Activity secondActivity = createActivity(request.getSecondRequest(), connectedUser);

        if (request.isAddressSeparated()) {
            setActivityAddress(request.getFirstRequest(), firstActivity);
            setActivityAddress(request.getSecondRequest(), secondActivity);
        } else {
            setActivityAddress(request.getFirstRequest(), firstActivity);
            secondActivity.addAddress(firstActivity.getAddress());
        }

        List<Activity> activities = List.of(firstActivity, secondActivity);

        firstActivity.setAssociatedId(secondActivity.getActivityId());
        secondActivity.setAssociatedId(firstActivity.getActivityId());

        activityRepository.saveAll(activities);
    }

    private void setActivityAddress(@NotNull ActivityRequest request, Activity activity) {
        Address address;
        if (request.getAddressRequest() != null) {
            address = AddressMapper.toAddress(request.getAddressRequest());
            addressService.save(address);
            activity.setAddress(address);
        }
    }

    @Transactional
    public Activity createActivity(@NotNull ActivityRequest request, UserDetails connectedUser) {
        if (request.getTripId() == null) {
            throw new TripNotFoundException("Trip ID is required");
        }
        if (request.getDateTime() == null) {
            throw new DateTimeException("Date and Time is required");
        }

        LocalDate requestedDateTime = toLocalDateTime(request.getDateTime()).toLocalDate();
        Day day = dayService.getByTripIdAndDate(request.getTripId(), requestedDateTime, connectedUser);

        Activity activity = ActivityMapper.toActivity(request);
        day.addActivity(activity);
        activityRepository.save(activity);

        return activity;
    }

    @Transactional
    public MessageResponse updateActivity(@NotNull ActivityUpdateRequest request, UserDetails connectedUser) {
        Activity activity = getById(request.getActivityId());
        activityAuthorizationService.verifyActivityOwner(activity, connectedUser);
        ActivityMapper.updateActivity(activity, request);
        updateActivityExpanseCategoryDescription(activity, connectedUser);
        String message;
        if (!toLocalDate(request.getOldDate()).equals(toLocalDate(request.getNewDate()))) {
            updateActivityDate(activity, request.getNewDate(), connectedUser);
            updateActivityExpanseDate(activity, toLocalDate(request.getNewDate()), connectedUser);
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
            activityAuthorizationService.verifyActivityOwner(associatedActivity, connectedUser);
            associatedActivity.setType(activity.getType());
            updateActivityExpanseCategoryDescription(activity, connectedUser);
            activityRepository.saveAll(List.of(activity, associatedActivity));
        } catch (ActivityNotFoundException _) {
            activityRepository.save(activity);
            log.error("Associated activity not found");
        }
    }

    public List<Activity> getActivitiesByDayId(Long dayId, UserDetails connectedUser) {
        List<Activity> activities = activityRepository.findByDayDayIdOrderByBeginTimeAsc(dayId);
        activityAuthorizationService.checkActivitiesAuthorization(new HashSet<>(activities), connectedUser);
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
                .map(country -> Country.builder()
                        .name(country.country())
                        .currency(country.currency())
                        .iso2(country.countryCode())
                        .iso3(country.countryCode())
                        .build())
                .filter(country -> country.getName() != null && !country.getName().isEmpty())
                .forEach(countries::add);
        return countries;
    }

    private @NotNull List<City> getActivitiesCitiesFromDay(@NotNull List<ActivityResponse> activityResponses) {
        List<City> cities = new ArrayList<>();
        activityResponses.stream()
                .map(activity -> activity.getAddress().city())
                .filter(Objects::nonNull)
                .map(city -> City.builder().city(city).build())
                .forEach(cities::add);
        return cities;
    }


    public AddressDetail buildAddressDetail(@NotNull List<Country> countries, @NotNull List<City> cities) {
        List<Country> filteredCountries = countries.stream()
                .collect(Collectors.toMap (
                        Country::getName,
                        country -> country,
                        (existing, _) -> existing,
                        LinkedHashMap::new
                ))
                .values().stream()
                .toList();

        List<City> filteredCities = cities.stream()
                .collect(Collectors.toMap(
                        City::getCity,
                        city -> city,
                        (existing, _) -> existing,
                        LinkedHashMap::new
                ))
                .values().stream()
                .toList();
        return new AddressDetail(
                filteredCountries,
                countries.stream().reduce((_, second) -> second).orElse(null),
                filteredCities,
                cities.stream().reduce((_, second) -> second).orElse(null)
        );
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
        activityAuthorizationService.verifyActivityOwner(activity, connectedUser);
        deleteActivities(Set.of(activity));
    }

    @Transactional
    protected void deleteActivities(@NotNull Set<Activity> activities) {
        Set<Long> activityIds = activities.stream()
                .map(Activity::getActivityId)
                .collect(Collectors.toSet());

        Set<Long> associatedIds = activities.stream()
                .map(Activity::getAssociatedId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        activityIds.addAll(associatedIds);

        Set<Activity> allActivities = activityRepository.findAllByActivityIdIn(activityIds);
        Set<Long> allAddressIds = allActivities.stream()
                .filter(activity -> activity.getAddress() != null)
                .map(activity -> activity.getAddress().getAddressId())
                .collect(Collectors.toSet());
        activityRepository.deleteAll(allActivities);
        addressService.deleteAllByAddressIdIn(allAddressIds);
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

    private void updateActivityExpanseCategoryDescription(@NotNull Activity activity, UserDetails connectedUser) {
        if(activity.getExpanse() == null) {
            return;
        }
        StringBuilder description = createExpanseCategoryDescription(activity);
        expanseService.updateExpanseCategory(activity.getExpanse(), description.toString(), connectedUser);
    }

    private void updateActivityExpanseDate(@NotNull Activity activity, LocalDate date, UserDetails connectedUser) {
        if(activity.getExpanse() == null) {
            return;
        }
        expanseService.updateExpanseDate(activity.getExpanse(), date, connectedUser );
    }
}
