package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.address.AddressMapper;
import com.andwis.travel_with_anna.address.AddressResponse;
import com.andwis.travel_with_anna.trip.expanse.ExpanseMapper;
import com.andwis.travel_with_anna.trip.note.NoteMapper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.andwis.travel_with_anna.utility.DateTimeMapper.toLocalDateTime;
import static com.andwis.travel_with_anna.utility.DateTimeMapper.toTime;

public class ActivityMapper {

    private static final AddressResponse DEFAULT_ADDRESS = new AddressResponse(
            -1L, "", "", "", "", "", "", "", "", ""
    );

    public static @NotNull ActivityResponse toActivityResponse(@NotNull Activity activity) {
        ActivityResponse activityResponse = ActivityResponse.builder()
                .activityId(activity.getActivityId())
                .activityTitle(activity.getActivityTitle())
                .startTime(activity.getFormattedBeginTime())
                .endTime(activity.getFormattedEndTime())
                .badge(activity.getBadge())
                .type(activity.getType())
                .status(activity.getStatus())
                .associatedId(activity.getAssociatedId())
                .dayTag(activity.isDayTag())
                .build();

        activityResponse.setNote(activity.getNote() != null
                ? NoteMapper.toNoteResponse(activity.getNote())
                : null);

        activityResponse.setExpanse(activity.getExpanse() != null
                ? ExpanseMapper.toExpanseResponse(activity.getExpanse())
                : null);

        activityResponse.setAddress(resolveDefaultAddress(activity.getAddress()));

        return  activityResponse;
    }

    public static Activity toActivity(@NotNull ActivityRequest request) {
        return Activity.builder()
                .beginTime(toLocalDateTime(request.getDateTime()).toLocalTime())
                .badge(request.getBadge())
                .type(request.getType())
                .status(request.getStatus())
                .activityTitle(request.getActivityTitle())
                .dayTag(request.isDayTag())
                .endTime(request.getEndTime() != null && !request.getEndTime().isEmpty()
                        ? toTime(request.getEndTime())
                        : null)
                .build();
    }

    public static List<ActivityResponse> toActivityResponseList(@NotNull List<Activity> activities) {
        return activities.stream().map(ActivityMapper::toActivityResponse).toList();
    }

    public static void updateActivity(@NotNull Activity activity, @NotNull ActivityUpdateRequest request) {
        activity.setActivityTitle(request.getActivityTitle());
        activity.setBeginTime(toTime(request.getStartTime()));
        activity.setType(request.getType());
        activity.setDayTag(request.isDayTag());
        activity.setEndTime(request.getEndTime() != null ? toTime(request.getEndTime()) : null);

        if (request.getAddressRequest() != null) {
            AddressMapper.updateExistingAddress(activity.getAddress(), request.getAddressRequest());
        }
    }

    private static AddressResponse resolveDefaultAddress(Address address) {
        return address != null ? AddressMapper.toAddressResponse(address) : DEFAULT_ADDRESS;
    }
}
