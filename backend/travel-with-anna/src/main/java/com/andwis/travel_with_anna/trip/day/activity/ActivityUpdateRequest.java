package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressRequest;
import jakarta.validation.Valid;
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
public class ActivityUpdateRequest {

    private static final String REQUIRED_MESSAGE = "is required";
    private static final String DATE_FORMAT_MESSAGE = "in format yyyy-MM-dd";
    private static final String TIME_FORMAT_MESSAGE = "in format HH:mm";


    @NotNull(message = "Activity ID " + REQUIRED_MESSAGE)
    private Long activityId;

    @NotNull(message = "Day ID " + REQUIRED_MESSAGE)
    private Long dayId;

    @NotNull(message = "Old Activity Date " + REQUIRED_MESSAGE + " " + DATE_FORMAT_MESSAGE)
    private String oldActivityDate;

    @NotNull(message = "New Activity Date " + REQUIRED_MESSAGE + " " + DATE_FORMAT_MESSAGE)
    private String newActivityDate;

    @NotNull(message = "Start Time " + REQUIRED_MESSAGE + " " + TIME_FORMAT_MESSAGE)
    private String startTime;

    private String endTime;

    @Size(max = MAX_TITLE_LENGTH, message = "Description should be " + MAX_TITLE_LENGTH + " characters or less")
    private String activityTitle;

    @Size(max = MAX_LENGTH, message = "Type should be " + MAX_LENGTH +  " characters or less")
    private String type;

    private @Valid AddressRequest addressRequest;

    private boolean isDayTag;
}
