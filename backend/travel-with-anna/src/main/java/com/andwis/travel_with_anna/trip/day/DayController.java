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

    @PostMapping("/create")
    public ResponseEntity<Void> createDay(@RequestBody @Valid DayRequest request) {
        facade.createDay(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addDay(@RequestBody DayAddRequest request) {
        facade.addDay(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{dayId}/day")
    public ResponseEntity<DayResponse> getDayById(@PathVariable("dayId") Long dayId) {
        return ResponseEntity.ok(facade.getDayById(dayId));
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<List<DayResponse>> getDays(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok(facade.getDays(tripId));
    }

    @PostMapping("/generate")
    public ResponseEntity<Void> generateDays(@RequestBody @Valid DayGeneratorRequest request) {
        facade.generateDays(request);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/{dayId}")
    public ResponseEntity<Void> deleteDay(@PathVariable("dayId") Long dayId) {
        facade.deleteDay(dayId);
        return ResponseEntity.noContent().build();
    }
}
