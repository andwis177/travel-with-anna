package com.andwis.travel_with_anna.trip.day;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("day")
@RequiredArgsConstructor
@Tag(name = "Day")
public class DayController {
    private final DayFacade facade;

    @PostMapping("/add")
    public ResponseEntity<Void> addDay(@RequestBody DayAddDeleteRequest request) {
        facade.addDay(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{dayId}")
    public ResponseEntity<DayResponse> getDayById(@PathVariable("dayId") Long dayId) {
        DayResponse dayResponse = facade.getDayById(dayId);
        return ResponseEntity.ok(dayResponse);
    }

    @GetMapping("/trip/{tripId}")
    public ResponseEntity<List<DayResponse>> getDays(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok(facade.getDays(tripId));
    }

    @PostMapping("/generate")
    public ResponseEntity<Void> generateDays(@RequestBody @Valid DayGeneratorRequest request) {
        facade.generateDays(request);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/change/date")
    public ResponseEntity<Void> changeDayDate(@RequestBody @Valid DayRequest request) {
        facade.changeDayDate(request);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteDay(@RequestBody @Valid DayAddDeleteRequest request) {
        facade.deleteDay(request);
        return ResponseEntity.noContent().build();
    }
}
