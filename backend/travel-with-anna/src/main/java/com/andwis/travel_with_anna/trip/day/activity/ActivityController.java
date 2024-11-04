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

    @PostMapping("/create")
    public ResponseEntity<Void> createActivity(
            @RequestBody @Valid ActivityRequest request) {
        facade.createSingleActivity(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/create/associated")
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

    @GetMapping("/get/{dayId}")
    public ResponseEntity<ActivityDetailedResponse> fetchActivitiesByDayId(
            @PathVariable("dayId") Long dayId) {
        ActivityDetailedResponse response = facade.fetchActivitiesByDayId(dayId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get/{dayId}/day/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByDayId(
            @PathVariable("dayId") Long dayId) {
        return ResponseEntity.ok(facade.fetchAddressDetailsByDayId(dayId));
    }

    @GetMapping("/get/{tripId}/trip/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByTripId(
            @PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok(facade.fetchAddressDetailsByTripId(tripId));
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivityById(
            @PathVariable("activityId") Long activityId) {
        facade.deleteActivityById(activityId);
        return ResponseEntity.noContent().build();
    }
}
