package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressRequest;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActivityRequest {
    @NotNull(message = "Trip ID is required")
    private Long tripId;
    @NotNull(message = "Date and Time is required in format yyyy-MM-dd'T'HH:mm")
    private String dateTime;
    private String endTime;
    @Size(max = 60, message = "Title should be 60 characters or less")
    private String activityTitle;
    @Size(max = 20, message = "Badge name should be 20 characters or less")
    private String badge;
    @Size(max = 20, message = "Type should be 20 characters or less")
    private String type;
    @Size(max = 20, message = "Status should be 20 characters or less")
    private String status;
    private AddressRequest addressRequest;
    private boolean isDayTag;
}
