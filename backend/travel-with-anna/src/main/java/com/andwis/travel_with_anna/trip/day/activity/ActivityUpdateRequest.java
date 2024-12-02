package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActivityUpdateRequest {
    @NotNull(message = "Activity ID is required")
    private Long activityId;
    @NotNull(message = "Day ID is required")
    private Long dayId;
    @NotNull(message = "Old Date is required in format yyyy-MM-dd")
    private String oldDate;
    @NotNull(message = "New Date is required in format yyyy-MM-dd")
    private String newDate;
    @NotNull(message = "Time is required in format HH:mm")
    private String startTime;
    private String endTime;
    @Size(max = 60, message = "Title should be 60 characters or less")
    private String activityTitle;
    @Size(max = 20, message = "Type should be 20 characters or less")
    private String type;
    private @Valid AddressRequest addressRequest;
    private boolean isDayTag;
}
