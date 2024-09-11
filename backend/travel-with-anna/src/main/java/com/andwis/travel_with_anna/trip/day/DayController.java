package com.andwis.travel_with_anna.trip.day;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("day")
@RequiredArgsConstructor
@Tag(name = "Day")
public class DayController {
    private final DayFacade facade;

    @PostMapping("/create")
    public ResponseEntity<Void> saveDay(Day day) {
        facade.saveDay(day);
        return ResponseEntity.accepted().build();
    }
}
