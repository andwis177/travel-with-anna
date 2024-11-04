package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressMapper;
import com.andwis.travel_with_anna.address.AddressResponse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseMapper;
import com.andwis.travel_with_anna.trip.note.NoteMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDateTime;
import static com.andwis.travel_with_anna.utility.DateTimeMapper.toTime;

public class ActivityMapper {

    public static @NotNull ActivityResponse toActivityResponse(@NotNull Activity activity) {
        ActivityResponse response =  ActivityResponse.builder()
                .activityId(activity.getActivityId())
                .activityTitle(activity.getActivityTitle())
                .startTime(activity.getBeginTime())
                .endTime(activity.getEndTime())
                .badge(activity.getBadge())
                .type(activity.getType())
                .status(activity.getStatus())
                .associatedId(activity.getAssociatedId())
                .isDayTag(activity.isDayTag())
                .build();
        if (activity.getNote() != null) {
            response.setNote(NoteMapper.toNoteResponse(activity.getNote()));
        }
        if (activity.getExpanse() != null) {
            response.setExpanse(ExpanseMapper.toExpanseResponse(activity.getExpanse()));
        }
        if (activity.getAddress() != null) {
            response.setAddress(AddressMapper.toAddressResponse(activity.getAddress()));
        } else {
            response.setAddress(new AddressResponse(
                    -1L,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""
            ));
        }
        return response;
    }

    public static@NotNull Activity toActivity(@NotNull ActivityRequest request) {
        Activity activity = Activity.builder()
                .beginTime(toLocalDateTime(request.getDateTime()).toLocalTime())
                .badge(request.getBadge())
                .type(request.getType())
                .status(request.getStatus())
                .activityTitle(request.getActivityTitle())
                .isDayTag(request.isDayTag())
                .build();

        if (request.getEndTime() != null && !request.getEndTime().isEmpty()) {
            activity.setEndTime(toTime(request.getEndTime()));
        }
        return activity;
    }

    public static List<ActivityResponse> toActivityResponseList(@NotNull Set<Activity> activities) {
        return activities.stream().map(ActivityMapper::toActivityResponse).toList();
    }

    public static void updateActivity(@NotNull Activity activity, @NotNull ActivityUpdateRequest request) {
        activity.setActivityTitle(request.getActivityTitle());
        activity.setBeginTime(toTime(request.getStartTime()));
        activity.setType(request.getType());
        activity.setDayTag(request.isDayTag());

        if (request.getEndTime() != null) {
            activity.setEndTime(toTime(request.getEndTime()));
        }

        if (request.getAddressRequest() != null) {
            AddressMapper.updateAddress(activity.getAddress(),request.getAddressRequest());
        }
    }
}
