package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.handler.exception.DayNotFoundException;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import com.andwis.travel_with_anna.utility.NumberDistributor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class DayService {
    private final DayRepository dayRepository;
    private final TripService tripService;

    public void saveDay(Day day) {
        dayRepository.save(day);
    }

    public void saveAllDays(Set<Day> days) {
        dayRepository.saveAll(days);
    }

    @Transactional
    public void createDay(@NotNull DayRequest request) {
        Trip trip = tripService.getTripById(request.getEntityId());
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

    public Set<Day> getDaysByTripId(Long tripId) {
        return dayRepository.findByTripTripId(tripId);
    }

    public Day getByTripIdAndDate(Long tripId, LocalDate date) {
        return dayRepository.findByTripTripIdAndDate(tripId, date)
                .orElseThrow(() -> new DayNotFoundException("Day not found"));
    }

    public DayResponse getDayById(Long dayId) {
        Day day = getById(dayId);
        return DayMapper.toDayResponse(day);
    }

    @Transactional
    public void addDay (@NotNull DayAddDeleteRequest request) {
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(request.getTripId());
        LocalDate dayToAdd = getDayToAdd(days, request.isFirst());
        DayRequest dayRequest = DayRequest.builder()
                .date(dayToAdd)
                .entityId(request.getTripId())
                .build();
        createDay(dayRequest);
    }

    private LocalDate getDayToAdd(@NotNull List<Day> days, boolean isFirst) {
        LocalDate dayToAdd;
        if (!days.isEmpty()) {
            if (isFirst) {
                dayToAdd = days.getFirst().getDate().minusDays(1);
            } else {
                dayToAdd = days.getLast().getDate().plusDays(1);
            }
            return dayToAdd;
        }
        return LocalDate.now();
    }

    public Set<Day> getDaysByTripIds(Set<Long> tripIds) {
        return dayRepository.findByTripTripIdIn(tripIds);
    }

    public List<DayResponse> getDays(Long tripId) {
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(tripId);
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
    public void generateDays(@NotNull DayGeneratorRequest request) {
        validateDates(request.getStartDate(), request.getEndDate());
        Trip trip = tripService.getTripById(request.getTripId());

        List<Day> days = createDays(request.getStartDate(), request.getEndDate());
        trip.addDays(days);
        tripService.saveTrip(trip);
    }

    private void validateDates(@NotNull LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start Date must be before End Date");
        }
    }

    @Transactional
    public void changeDayDate(@NotNull DayRequest request) {
        if (request.getEntityId() == null) {
            throw new IllegalArgumentException("Day Id must be provided");
        }
        if (request.getDate() == null) {
            throw new IllegalArgumentException("Date must be provided");
        }
        Day day = getById(request.getEntityId());
        day.setDate(request.getDate());
        saveDay(day);
    }

    @Transactional
    public void changeTripDates(@NotNull Trip trip, LocalDate startDate, LocalDate endDate) {
        validateDates(startDate, endDate);

        List<Day> existingDays  = dayRepository.findByTripTripIdOrderByDateAsc(trip.getTripId());

        long totalNewDays = ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long existingDaysSize = existingDays.size();

        for (int i = 0; i < Math.min(existingDaysSize, totalNewDays); i++) {
            existingDays.get(i).setDate(startDate.plusDays(i));
        }

        if (totalNewDays > existingDaysSize) {
            List<Day> newDays = new ArrayList<>(existingDays);
            newDays.addAll(createDays(startDate.plusDays((int) existingDaysSize), endDate));

            trip.addDays(newDays);
        }
    }

    @Transactional
    public void deleteDay(@NotNull Day day, @NotNull Consumer<Day> deleteFunction) {
        deleteFunction.accept(day);
        day.getTrip().getDays().remove(day);
        dayRepository.delete(day);
    }

    @Transactional
    public void deleteFirstOrLastDay (@NotNull DayAddDeleteRequest request, Consumer<Day> deleteFunction) {
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(request.getTripId());
        if (request.isFirst()) {
            deleteDay(days.getFirst(), deleteFunction);
        } else {
            deleteDay(days.getLast(), deleteFunction);
        }
    }
}
