package com.andwis.travel_with_anna.trip.day.activity.badge;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("badge")
@Tag(name = "Badge")
public class BadgeController {

    @GetMapping("")
    public List<String> getBadges() {
        return Badge.getBadges();
    }
}
