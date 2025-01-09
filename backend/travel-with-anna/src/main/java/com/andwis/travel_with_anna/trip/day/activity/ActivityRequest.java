package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import static com.andwis.travel_with_anna.trip.day.activity.Activity.MAX_LENGTH;
import static com.andwis.travel_with_anna.trip.day.activity.Activity.MAX_TITLE_LENGTH;

@Getter
@Setter
@Builder
public class ActivityRequest {

    @NotNull(message = "Trip ID is required")
    private Long tripId;

    @NotNull(message = "Date and Time is required in format yyyy-MM-dd'T'HH:mm")
    private String dateTime;

    private String endTime;

    @Size(max = MAX_TITLE_LENGTH, message = "Description should be " + MAX_TITLE_LENGTH + " characters or less")
    private String activityTitle;

    @Size(max = MAX_LENGTH, message = "Badge name should be " + MAX_LENGTH + " characters or less")
    private String badge;

    @NotNull(message = "Please select activity type")
    @NotEmpty(message = "Please select activity type")
    @NotBlank(message = "Please select activity type")
    @Size(max = MAX_LENGTH, message = "Type should be  " + MAX_LENGTH + "  characters or less")
    private String type;

    @Size(max = MAX_LENGTH, message = "Status should be " + MAX_LENGTH + " characters or less")
    private String status;

    private @Valid AddressRequest addressRequest;

    private boolean dayTag;
}
