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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DateTimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDate;
import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final DayService dayService;
    private final AddressService addressService;

    public Activity findById(Long activityId) {
        return activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException("Activity not found"));
    }

    public void createSingleActivity(ActivityRequest request) {
        Address address;
        Activity activity = createActivity(request);

        if (request.getAddressRequest() != null) {
            address = AddressMapper.toAddress(request.getAddressRequest());
            addressService.save(address);
            address.addActivity(activity);
        }
        activityRepository.save(activity);
    }

    public void createAssociatedActivities(ActivityRequest firstRequest, ActivityRequest secondRequest) {
        Address address;
        Activity firstActivity = createActivity(firstRequest);
        Activity secondActivity = createActivity(secondRequest);

        if (firstRequest.getAddressRequest() != null) {
            address = AddressMapper.toAddress(firstRequest.getAddressRequest());
            addressService.save(address);
            address.addActivity(firstActivity);
            address.addActivity(secondActivity);
        }

        List<Activity> activities = List.of(
                firstActivity,
                secondActivity);

        firstActivity.setAssociatedId(secondActivity.getActivityId());
        secondActivity.setAssociatedId(firstActivity.getActivityId());

        activityRepository.saveAll(activities);

    }

    public Activity createActivity(@NotNull ActivityRequest request) {
        if (request.getTripId() == null) {
            throw new TripNotFoundException("Trip ID is required");
        }
        if (request.getDateTime() == null) {
            throw new DateTimeException("Date and Time is required");
        }

        Day day = dayService.findByTripIdAndDate(request.getTripId(),
                toLocalDateTime(request.getDateTime()).toLocalDate());

        Activity activity = ActivityMapper.toActivity(request);
        day.addActivity(activity);
        activityRepository.save(activity);

        return activity;
    }

    public void updateActivity(@NotNull ActivityUpdateRequest request) {
        Activity activity = findById(request.getActivityId());
        ActivityMapper.updateActivity(activity, request);

        if (!toLocalDate(request.getOldDate()).equals(toLocalDate(request.getNewDate()))) {
            updateActivityDate(activity, request.getNewDate());
        }

        if (activity.getAssociatedId() != null) {
            updateAssociatedActivity(activity, request.getType());
        } else {
            activityRepository.save(activity);
        }
    }

    private void updateActivityDate(Activity activity, String newDate) {
        Day oldDay = activity.getDay();
        Day newDay = dayService.findByTripIdAndDate(
                activity.getDay().getTrip().getTripId(),
                toLocalDate(newDate));
        oldDay.removeActivity(activity);
        newDay.addActivity(activity);
        dayService.saveAllDays(List.of(oldDay, newDay));
    }

    private void updateAssociatedActivity(Activity activity, String newType) {
        try {
            Activity associatedActivity = findById(activity.getAssociatedId());
            associatedActivity.setType(newType);
            activityRepository.saveAll(List.of(activity, associatedActivity));
        } catch (ActivityNotFoundException _) {
            activityRepository.save(activity);
            log.error("Associated activity not found");
        }
    }

    public List<Activity> getActivitiesByDayId(Long dayId) {
        return activityRepository.findByDayDayIdOrderByBeginTimeAsc(dayId);
    }

    public ActivityDetailedResponse fetchActivitiesByDayId(Long dayId) {
        List<Activity> activities = getActivitiesByDayId(dayId);
        List<ActivityResponse> activityResponses = ActivityMapper.toActivityResponseList(activities);
        AddressDetail addressDetail = buildAddressDetail(
                getActivitiesCountriesFromDay(activityResponses),
                getActivitiesCitiesFromDay(activityResponses)
        );

        return new ActivityDetailedResponse(
                addressDetail,
                activityResponses,
                getTotalAmount(activityResponses, ExpanseResponse::priceInTripCurrency),
                getTotalAmount(activityResponses, ExpanseResponse::paidInTripCurrency)
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
        activityResponses.forEach(activity -> {
            if (activity.getAddress().country() != null && !activity.getAddress().country().isEmpty()) {
                Country country = Country.builder()
                        .name(activity.getAddress().country())
                        .iso2(activity.getAddress().countryCode())
                        .currency(activity.getAddress().currency())
                        .build();
                addToCountryIfNotExists(countries, country);
            }
        });
        return countries;
    }

    private void addToCountryIfNotExists(@NotNull List<Country> countries, Country country) {
        if (!countries.contains(country)) {
            countries.add(country);
        }
    }

    private @NotNull List<City> getActivitiesCitiesFromDay(@NotNull List<ActivityResponse> activityResponses) {
        List<City> cities = new ArrayList<>();
        activityResponses.stream()
                .map(activity -> activity.getAddress().city())
                .filter(city -> city != null && !city.isEmpty())
                .map(city -> City.builder().city(city).build())
                .forEach(city -> addToCityIfNotExists(cities, city));
        return cities;
    }

    private void addToCityIfNotExists(@NotNull List<City> cities, City city) {
        if (!cities.contains(city)) {
            cities.add(city);
        }
    }

    public AddressDetail buildAddressDetail(List<Country> countries, List<City> cities) {
        return new AddressDetail(
                countries,
                countries.stream().reduce((first, second) -> second).orElse(null),
                cities,
                cities.stream().reduce((first, second) -> second).orElse(null)
        );
    }

    public AddressDetail fetchAddressDetailByDayId(Long dayId) {
        List<Activity> activities = getActivitiesByDayId(dayId);
        List<ActivityResponse> activityResponses = ActivityMapper.toActivityResponseList(activities);
        return  buildAddressDetail(
                getActivitiesCountriesFromDay(activityResponses),
                getActivitiesCitiesFromDay(activityResponses)
        );
    }

    public AddressDetail fetchAddressDetailByTripId(Long tripId) {
        List<DayResponse> days = dayService.getDays(tripId);
        List<ActivityResponse> activityResponses = new ArrayList<>();
        for (DayResponse day : days) {
            List<Activity> activities = getActivitiesByDayId(day.dayId());
            activityResponses.addAll(ActivityMapper.toActivityResponseList(activities));
        }
        return  buildAddressDetail(
                getActivitiesCountriesFromDay(activityResponses),
                getActivitiesCitiesFromDay(activityResponses)
        );
    }

    public void deleteActivity(@NotNull Long activityId) {
        Activity activity = findById(activityId);
        Day day = activity.getDay();

        Optional<Activity> associatedActivity = findAssociatedActivity(activity.getAssociatedId());

        associatedActivity.ifPresentOrElse(
                assocActivity -> deleteBothActivities(activity, day, assocActivity),
                () -> deleteSingleActivity(activity, day)
        );
    }

    private Optional<Activity> findAssociatedActivity(Long associatedId) {
        return Optional.ofNullable(associatedId)
                .map(id -> {
                    try {
                        return findById(id);
                    } catch (ActivityNotFoundException e) {
                        log.error("Associated activity not found");
                        return null;
                    }
                });
    }

    private void deleteBothActivities(Activity activity, Day day, Activity associatedActivity) {
        Day associatedDay = associatedActivity.getDay();
        day.removeActivity(activity);
        associatedDay.removeActivity(associatedActivity);

        activityRepository.deleteAll(List.of(activity, associatedActivity));
        dayService.saveAllDays(List.of(day, associatedDay));
    }

    private void deleteSingleActivity(Activity activity, Day day) {
        day.removeActivity(activity);
        activityRepository.delete(activity);
        dayService.saveDay(day);
    }
}
