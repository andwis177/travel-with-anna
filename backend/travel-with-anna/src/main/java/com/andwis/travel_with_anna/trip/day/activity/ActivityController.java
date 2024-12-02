package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.utility.MessageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("activity")
@RequiredArgsConstructor
@Tag(name = "Activity")

public class ActivityController {
    private final ActivityFacade facade;

    @PostMapping()
    public ResponseEntity<Void> createActivity(
            @RequestBody @Valid ActivityRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.createSingleActivity(request, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/associated")
    public ResponseEntity<Void> createAssociatedActivities(
            @RequestBody @Valid ActivityAssociatedRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.createAssociatedActivities(request, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @PatchMapping("/update")
    public ResponseEntity<MessageResponse> updateActivity(
            @RequestBody @Valid ActivityUpdateRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        MessageResponse message = facade.updateActivity(request, connectedUser);
        return ResponseEntity.accepted().body(message);
    }

    @GetMapping("/day/{dayId}")
    public ResponseEntity<ActivityDetailedResponse> fetchActivitiesByDayId(
            @PathVariable("dayId") Long dayId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        ActivityDetailedResponse response = facade.fetchActivitiesByDayId(dayId, connectedUser);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/day/{dayId}/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByDayId(
            @PathVariable("dayId") Long dayId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        AddressDetail addressDetail = facade.fetchAddressDetailsByDayId(dayId, connectedUser);
        return ResponseEntity.ok(addressDetail);
    }

    @GetMapping("/trip/{tripId}/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByTripId(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        AddressDetail addressDetail = facade.fetchAddressDetailsByTripId(tripId, connectedUser);
        return ResponseEntity.ok(addressDetail);
    }

    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivityById(
            @PathVariable("activityId") Long activityId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.deleteActivityById(activityId, connectedUser);
        return ResponseEntity.noContent().build();
    }
}
