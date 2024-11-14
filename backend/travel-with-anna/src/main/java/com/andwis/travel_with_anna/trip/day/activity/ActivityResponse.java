package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressResponse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseResponse;
import com.andwis.travel_with_anna.trip.note.NoteResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ActivityResponse {
    private Long activityId;
    private String activityTitle;
    private String startTime;
    private String endTime;
    private String badge;
    private String type;
    private String status;
    private NoteResponse note;
    private ExpanseResponse expanse;
    private Long associatedId;
    private AddressResponse address;
    private boolean isDayTag;

}
