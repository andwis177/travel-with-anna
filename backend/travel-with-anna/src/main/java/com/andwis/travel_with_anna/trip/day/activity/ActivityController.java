package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.utility.MessageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("activity")
@RequiredArgsConstructor
@Tag(name = "Activity")

public class ActivityController {
    private final ActivityFacade facade;

    @PostMapping()
    public ResponseEntity<Void> createActivity(
            @RequestBody @Valid ActivityRequest request) {
        facade.createSingleActivity(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/associated")
    public ResponseEntity<Void> createAssociatedActivities(
            @RequestBody @Valid ActivityAssociatedRequest request) {
        facade.createAssociatedActivities(request);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<MessageResponse> updateActivity(
            @RequestBody @Valid ActivityUpdateRequest request) {
        MessageResponse message = facade.updateActivity(request);
        return ResponseEntity.accepted().body(message);
    }

    @GetMapping("/day/{dayId}")
    public ResponseEntity<ActivityDetailedResponse> fetchActivitiesByDayId(
            @PathVariable("dayId") Long dayId) {
        ActivityDetailedResponse response = facade.fetchActivitiesByDayId(dayId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/day/{dayId}/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByDayId(
            @PathVariable("dayId") Long dayId) {
        AddressDetail addressDetail = facade.fetchAddressDetailsByDayId(dayId);
        return ResponseEntity.ok(addressDetail);
    }

    @GetMapping("/trip/{tripId}/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByTripId(
            @PathVariable("tripId") Long tripId) {
        AddressDetail addressDetail = facade.fetchAddressDetailsByTripId(tripId);
        return ResponseEntity.ok(addressDetail);
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivityById(
            @PathVariable("activityId") Long activityId) {
        facade.deleteActivityById(activityId);
        return ResponseEntity.noContent().build();
    }
}
