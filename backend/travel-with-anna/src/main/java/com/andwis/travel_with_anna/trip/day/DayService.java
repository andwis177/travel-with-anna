package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.handler.exception.DayNotFoundException;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import com.andwis.travel_with_anna.utility.NumberDistributor;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DayService {
    private final DayRepository dayRepository;
    private final TripService tripService;

    public void saveDay(Day day) {
        dayRepository.save(day);
    }
    public void saveAllDays(List<Day> days) {
        dayRepository.saveAll(days);
    }

    public void createDay(@NotNull DayRequest request) {
        Trip trip = tripService.getTripById(request.getTripId());
        Day day = Day.builder()
                .date(request.getDate())
                .build();
        trip.addDay(day);
        saveDay(day);
    }

    public Day findById(Long dayId) {
        return dayRepository.findById(dayId)
                .orElseThrow(() -> new DayNotFoundException("Day not found"));
    }

    public Day findByTripIdAndDate(Long tripId, LocalDate date) {
        return dayRepository.findByTripTripIdAndDate(tripId, date)
                .orElseThrow(() -> new DayNotFoundException("Day not found"));
    }

    public DayResponse getDayById(Long dayId) {
        Day day = findById(dayId);
        return DayMapper.toDayResponse(day);
    }

    public void addDay (@NotNull DayAddRequest request) {
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(request.getTripId());
        LocalDate dayToAdd = getDayToAdd(days, request.isFirst());
        DayRequest dayRequest = DayRequest.builder()
                .date(dayToAdd)
                .tripId(request.getTripId())
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

    public List<DayResponse> getDays(Long tripId) {
        List<Day> days = dayRepository.findByTripTripIdOrderByDateAsc(tripId);
        List<DayResponse> response = days.stream()
                .map(DayMapper::toDayResponse)
                .toList();
        NumberDistributor.reset();
        return response;
    }

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

    public void deleteDay(Long dayId) {
        dayRepository.deleteById(dayId);
    }
}
