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

    private static final String INVALID_NOTE_TYPE_MESSAGE =
            "Invalid note type. Supported types are: %s. Note cannot be saved!";

    public final NoteService noteService;
    private final DayService dayService;
    private final ActivityService activityService;
    private final ActivityAuthorizationService activityAuthorizationService;
    private final DayAuthorizationService dayAuthorizationService;

    public void saveNote(@NotNull NoteRequest request, UserDetails connectedUser) {
        NoteType noteType = validateAndGetNoteTypeToSaveNote(request.getLinkedEntityType());
        switch (noteType) {
            case DAY -> handleSaveForDay(request, connectedUser);
            case ACTIVITY -> handleSaveForActivity(request, connectedUser);
        }
    }

    private @NotNull NoteType validateAndGetNoteTypeToSaveNote(String type) {
        NoteType noteType = NoteType.fromString(type);
        if (noteType == null) {
            String supportedTypes = Arrays.stream(NoteType.values())
                    .map(NoteType::getNoteType)
                    .toList()
                    .toString();
            throw new NoteTypeException(String.format(INVALID_NOTE_TYPE_MESSAGE, supportedTypes));
        }
        return noteType;
    }

    public NoteResponse getNote(@NotNull Long entityId, @NotBlank @NotEmpty String type,
                                UserDetails connectedUser) {
        NoteType noteType = NoteType.fromString(type);
        if (noteType == null) {
            return new NoteResponse(-1L, "");
        }
        return switch (noteType) {
            case DAY -> fetchNoteByDayId(entityId, connectedUser);
            case ACTIVITY -> fetchNoteByActivityId(entityId, connectedUser);
        };
    }

    private void handleSaveForDay(NoteRequest noteRequest, UserDetails connectedUser) {
        noteService.saveNote(
                noteRequest,
                dayService::getById,
                Day::getNote,
                Day::addNote,
                Day::removeNote,
                dayAuthorizationService::verifyDayOwner,
                connectedUser
        );
    }

    private void handleSaveForActivity(NoteRequest noteRequest, UserDetails connectedUser) {
        noteService.saveNote(
                noteRequest,
                activityService::getById,
                Activity::getNote,
                Activity::addNote,
                Activity::removeNote,
                activityAuthorizationService::authorizeSingleActivity,
                connectedUser
        );
    }

    private NoteResponse fetchNoteByDayId(Long dayId, UserDetails connectedUser) {
        return noteService.getNoteById(
                dayId,
                dayService::getById,
                Day::getNote,
                dayAuthorizationService::verifyDayOwner,
                connectedUser
        );
    }

    private NoteResponse fetchNoteByActivityId(Long activityId, UserDetails connectedUser) {
        return noteService.getNoteById(
                activityId,
                activityService::getById,
                Activity::getNote,
                activityAuthorizationService::authorizeSingleActivity,
                connectedUser
        );
    }
}
