package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.handler.exception.DayNotFoundException;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import com.andwis.travel_with_anna.utility.NumberDistributor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDate;

@Service
@RequiredArgsConstructor
public class DayService {

    private final DayRepository dayRepository;
    private final TripService tripService;
    private final DayAuthorizationService dayAuthorizationService;

    public void saveDay(Day day) {
        dayRepository.save(day);
    }

    public void saveAllDays(Set<Day> days) {
        dayRepository.saveAll(days);
    }

    @Transactional
    public void createDay(@NotNull DayRequest request) {
        Trip trip = request.getTrip();
        Day day = Day.builder()
                .date(request.getDate())
                .build();
        trip.addDay(day);
        saveDay(day);
    }

    public Day getById(Long dayId) {
        return dayRepository.findById(dayId)
                .orElseThrow(() -> new DayNotFoundException("Day not found"));
    }

    public Set<Day> getDaysByTripId(Long tripId, UserDetails connectedUser) {
        Set<Day> days = dayRepository.findByTripTripId(tripId);
        dayAuthorizationService.checkDaysAuthorization(days, connectedUser);
        return days;
    }

    public Day getByTripIdAndDate(Long tripId, LocalDate date, UserDetails connectedUser) {
        Day day = dayRepository.findByTripTripIdAndDate(tripId, date)
                .orElseThrow(() -> new DayNotFoundException("Day not found"));
        dayAuthorizationService.verifyDayOwner(day, connectedUser);
        return day;
    }

    public DayResponse getDayById(Long dayId, UserDetails connectedUser) {
        Day day = getById(dayId);
        dayAuthorizationService.verifyDayOwner(day, connectedUser);
        return DayMapper.toDayResponse(day);
    }

    @Transactional
    public void addDay (Long tripId, boolean isFirst, UserDetails connectedUser) {
        Trip trip = tripService.getTripById(tripId);
        dayAuthorizationService.verifyTripOwner(trip, connectedUser);

        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(tripId);
        LocalDate selectedDate = determineDayToAdd(days, isFirst);
        createAndSaveDay(trip, selectedDate);
    }

    private LocalDate determineDayToAdd(@NotNull List<Day> days, boolean isFirst) {
        final int ADJUSTMENT_DAY = 1;
        if (!days.isEmpty()) {
            return isFirst ? days.getFirst().getDate().minusDays(ADJUSTMENT_DAY)
                    : days.getLast().getDate().plusDays(ADJUSTMENT_DAY);
        }
        return LocalDate.now();
    }

    private void createAndSaveDay(@NotNull Trip trip, LocalDate date) {
        Day day = Day.builder()
                .date(date)
                .build();
        trip.addDay(day);
        saveDay(day);
    }

    public Set<Day> getDaysByTripIds(Set<Long> tripIds) {
        return dayRepository.findByTripTripIdIn(tripIds);
    }

    public List<DayResponse> getDays(Long tripId, UserDetails connectedUser) {
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(tripId);
        dayAuthorizationService.checkDaysAuthorization(Set.copyOf(days), connectedUser);
        List<DayResponse> response = days.stream()
                .map(DayMapper::toDayResponse)
                .toList();
        NumberDistributor.reset();
        return response;
    }

    @Transactional
    public List<Day> createDays(LocalDate startDate, LocalDate endDate) {
        long numberOfDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        List<Day> days = new ArrayList<>((int) numberOfDays);

        startDate.datesUntil(endDate.plusDays(1)).forEach(date ->
                days.add(Day.builder()
                        .date(date)
                        .build())
        );
        return days;
    }

    @Transactional
    public void generateDays(@NotNull DayGeneratorRequest request, UserDetails connectedUser) {
        validateDates(toLocalDate(request.getStartDate()), toLocalDate(request.getEndDate()));
        Trip trip = tripService.getTripById(request.getAssociatedTripId());
        dayAuthorizationService.verifyTripOwner(trip, connectedUser);

        List<Day> days = createDays(toLocalDate(request.getStartDate()), toLocalDate(request.getEndDate()));
        trip.replaceDays(days);
        tripService.saveTrip(trip);
    }

    private void validateDates(@NotNull LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start Date must be before End Date");
        }
    }

    @Transactional
    public void changeTripDates(@NotNull Trip trip, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        List<Day> existingDays  = dayRepository.findByTripTripIdOrderByDateAsc(trip.getTripId());
        long totalNewDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        adjustExistingDays(existingDays, startDate, totalNewDays);

        manageNotExistingDays(trip, totalNewDays, existingDays);
    }

    @Transactional
    protected void manageNotExistingDays(Trip trip, long totalNewDays, @NotNull List<Day> existingDays ) {
        if (totalNewDays > existingDays.size()) {
            whenMoreNewDays(trip, totalNewDays, existingDays);
        }
        if (totalNewDays < existingDays.size()) {
            adjustRemainingDays(existingDays, (int)totalNewDays);
        }
    }

    @Transactional
    protected void whenMoreNewDays(Trip trip, long totalNewDays, @NotNull List<Day> existingDays) {
        int daysToAdd = (int) (totalNewDays - existingDays.size()) - 1;
        LocalDate startDateAfterAdjustment = existingDays.getLast().getDate().plusDays(1);
        addNewDaysToTrip(trip, daysToAdd, startDateAfterAdjustment);
    }

    private void adjustExistingDays(@NotNull List<Day> existingDays, LocalDate startDate, long totalDays) {
        for (int i = 0; i < Math.min(existingDays.size(), totalDays); i++) {
            existingDays.get(i).setDate(startDate.plusDays(i));
        }
    }

    private void adjustRemainingDays(@NotNull List<Day> existingDays, int startingIndex) {
        LocalDate startDate = existingDays.getFirst().getDate();
        for (int i = startingIndex; i < existingDays.size(); i++ ) {
            existingDays.get(i).setDate(startDate.plusDays(i));
        }
    }

    @Transactional
    protected void addNewDaysToTrip(@NotNull Trip trip, int startOffset, @NotNull LocalDate startDate) {
        List<Day> newDays = createDays(startDate, startDate.plusDays(startOffset));
        trip.addDays(newDays);
    }

    public void deleteDay(@NotNull Day day, @NotNull Consumer<Day> deleteFunction) {
        deleteFunction.accept(day);
        day.getTrip().getDays().remove(day);
        dayRepository.delete(day);
    }

    @Transactional
    public void deleteFirstOrLastDay (
            Long tripId,
            boolean isFirst,
            Consumer<Day> deleteFunction,
            UserDetails connectedUser) {
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(tripId);
        dayAuthorizationService.checkDaysAuthorization(Set.copyOf(days), connectedUser);
        if (isFirst) {
            deleteDay(days.getFirst(), deleteFunction);
        } else {
            deleteDay(days.getLast(), deleteFunction);
        }
    }
}
