package com.andwis.travel_with_anna.trip.backpack;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("backpack")
@Tag(name = "Backpack")
public class BackpackController {
    private final BackpackService service;

    @PostMapping("/create")
    public ResponseEntity<Void> saveBackpack(Backpack backpack) {
        service.saveBackpack(backpack);
        return ResponseEntity.accepted().build();
    }
}
