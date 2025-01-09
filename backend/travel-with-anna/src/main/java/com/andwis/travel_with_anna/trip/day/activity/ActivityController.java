package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.utility.MessageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public void createActivity(
            @RequestBody @Valid ActivityRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.createSingleActivity(request, connectedUser);
    }

    @PostMapping("/associated")
    @ResponseStatus(HttpStatus.CREATED)
    public void createAssociatedActivities(
            @RequestBody @Valid ActivityAssociatedRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.createAssociatedActivities(request, connectedUser);
    }

    @PatchMapping("/update")
    public ResponseEntity<MessageResponse> updateActivity(
            @RequestBody @Valid ActivityUpdateRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.accepted().body(facade.updateActivity(request, connectedUser));
    }

    @GetMapping("/day/{dayId}")
    public ResponseEntity<ActivityDetailedResponse> fetchActivitiesByDayId(
            @PathVariable("dayId") Long dayId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.fetchActivitiesByDayId(dayId, connectedUser));
    }

    @GetMapping("/day/{dayId}/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByDayId(
            @PathVariable("dayId") Long dayId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.fetchAddressDetailsByDayId(dayId, connectedUser));
    }

    @GetMapping("/trip/{tripId}/details")
    public ResponseEntity<AddressDetail> fetchAddressDetailsByTripId(
            @PathVariable("tripId") Long tripId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.fetchAddressDetailsByTripId(tripId, connectedUser));
    }

    @DeleteMapping("/{activityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteActivityById(
            @PathVariable("activityId") Long activityId,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.deleteActivityById(activityId, connectedUser);
    }
}
