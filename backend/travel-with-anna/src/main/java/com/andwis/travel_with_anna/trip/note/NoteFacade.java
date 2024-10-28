package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.handler.exception.NoteTypeException;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import com.andwis.travel_with_anna.trip.trip.TripService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class NoteFacade {
    public final NoteService noteService;
    private final TripService tripService;
    private final DayService dayService;
    private final ActivityService activityService;


    public void saveNote(@NotNull NoteRequest noteRequest) {
        NoteType entityType = NoteType.fromString(noteRequest.getEntityType());

        if (entityType == null) {
            String[] noteTypes = Arrays.stream(NoteType.values())
                    .map(NoteType::getType)
                    .toArray(String[]::new);
            throw new NoteTypeException("Invalid note type. Supported types are: " + Arrays.toString(noteTypes) + " . Note can not be saved!");
        } else {
            switch (entityType) {
                case DAY -> saveNoteForDay(noteRequest);
                case ACTIVITY -> saveNoteForActivity(noteRequest);
            };
        }
    }

    public NoteResponse getNote(
            @NotNull Long entityId,
            @NotBlank @NotEmpty String type) {
        NoteType entityType = NoteType.fromString(type);
        if (entityType == null) {
            return new NoteResponse(-1L, "");
        }
        return switch (entityType) {
            case DAY -> getNoteByDayId(entityId);
            case ACTIVITY -> getNoteByActivityId(entityId);
        };
    }

    private void saveNoteForDay(NoteRequest noteRequest) {
        noteService.saveNote(noteRequest, dayService::getById, Day::getNote, Day::addNote);
    }

    private void saveNoteForActivity(NoteRequest noteRequest) {
        noteService.saveNote(noteRequest, activityService::findById, Activity::getNote, Activity::addNote);
    }

    private NoteResponse getNoteByDayId(Long dayId) {
        return noteService.getNoteById(dayId, dayService::getById, Day::getNote);
    }

    private NoteResponse getNoteByActivityId(Long activityId) {
        return noteService.getNoteById(activityId, activityService::findById, Activity::getNote);
    }
}
