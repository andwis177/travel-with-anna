package com.andwis.travel_with_anna.trip.day.activity.type.travel;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("traveler")
@RequiredArgsConstructor
@Tag(name = "Traveler")
public class TravelerController {
    private final TravelerService travelerService;

    @PostMapping("/save")
    public void saveTraveling() {
        travelerService.saveTraveler();
    }

}
