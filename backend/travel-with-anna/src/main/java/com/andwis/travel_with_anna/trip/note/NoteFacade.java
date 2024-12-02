package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.handler.exception.NoteTypeException;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayAuthorizationService;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityAuthorizationService;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class NoteFacade {
    public final NoteService noteService;
    private final DayService dayService;
    private final ActivityService activityService;
    private final ActivityAuthorizationService activityAuthorizationService;
    private final DayAuthorizationService dayAuthorizationService;

    public void saveNote(@NotNull NoteRequest noteRequest, UserDetails connectedUser) {
        NoteType entityType = NoteType.fromString(noteRequest.getEntityType());

        if (entityType == null) {
            String[] noteTypes = Arrays.stream(NoteType.values())
                    .map(NoteType::getType)
                    .toArray(String[]::new);
            throw new NoteTypeException("Invalid note type. Supported types are: " + Arrays.toString(noteTypes) + " . Note can not be saved!");
        } else {
            switch (entityType) {
                case DAY -> saveNoteForDay(noteRequest, connectedUser);
                case ACTIVITY -> saveNoteForActivity(noteRequest, connectedUser);
            }
        }
    }

    public NoteResponse getNote(
            @NotNull Long entityId,
            @NotBlank @NotEmpty String type,
            UserDetails connectedUser) {
        NoteType entityType = NoteType.fromString(type);
        if (entityType == null) {
            return new NoteResponse(-1L, "");
        }
        return switch (entityType) {
            case DAY -> getNoteByDayId(entityId, connectedUser);
            case ACTIVITY -> getNoteByActivityId(entityId, connectedUser);
        };
    }

    private void saveNoteForDay(NoteRequest noteRequest, UserDetails connectedUser) {
        noteService.saveNote(
                noteRequest,
                dayService::getById,
                Day::getNote,
                Day::addNote,
                Day::removeNote,
                dayAuthorizationService::verifyDayOwner,connectedUser
        );
    }

    private void saveNoteForActivity(NoteRequest noteRequest, UserDetails connectedUser) {
        noteService.saveNote(
                noteRequest,
                activityService::getById,
                Activity::getNote,
                Activity::addNote,
                Activity::removeNote,
                activityAuthorizationService::verifyActivityOwner,connectedUser
        );
    }

    private NoteResponse getNoteByDayId(Long dayId, UserDetails connectedUser) {
        return noteService.getNoteById(
                dayId,
                dayService::getById,
                Day::getNote,
                dayAuthorizationService::verifyDayOwner,connectedUser
        );
    }

    private NoteResponse getNoteByActivityId(Long activityId, UserDetails connectedUser) {
        return noteService.getNoteById(
                activityId,
                activityService::getById,
                Activity::getNote,
                activityAuthorizationService::verifyActivityOwner,connectedUser
        );
    }
}
