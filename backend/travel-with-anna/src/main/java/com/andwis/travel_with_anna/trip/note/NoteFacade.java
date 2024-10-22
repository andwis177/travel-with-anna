package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteFacade {
    public final NoteService noteService;
    private final TripService tripService;
    private final ActivityService activityService;

    public void saveNoteForTrip(NoteRequest noteRequest) {
        noteService.saveNote(noteRequest, tripService::getTripById, Trip::getNote, Trip::addNote);
    }

    public void saveNoteForActivity(NoteRequest noteRequest) {
        noteService.saveNote(noteRequest, activityService::findById, Activity::getNote, Activity::addNote);
    }

    public NoteResponse getNoteByTripId(Long tripId) {
        return noteService.getNoteById(tripId, tripService::getTripById, Trip::getNote);
    }

    public NoteResponse getNoteByActivityId(Long activityId) {
        return noteService.getNoteById(activityId, activityService::findById, Activity::getNote);
    }
}
