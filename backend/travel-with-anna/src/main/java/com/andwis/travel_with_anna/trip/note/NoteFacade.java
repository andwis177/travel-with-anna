package com.andwis.travel_with_anna.trip.note;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteFacade {
    public final NoteService noteService;

    public void createNewNoteForTrip(NoteForTripRequest noteRequest) {
        noteService.createNewNoteForTrip(noteRequest);
    }

    public NoteResponse getNoteById(Long tripId) {
        return noteService.getNoteById(tripId);
    }
}
