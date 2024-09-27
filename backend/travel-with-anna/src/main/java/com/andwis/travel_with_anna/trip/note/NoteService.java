package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;
    private final TripService tripService;

    public void saveNote(Note note) {
        noteRepository.save(note);
    }

    public boolean isNoteExists(Long noteId) {
        return noteRepository.existsById(noteId);
    }

    public NoteResponse getNoteById(Long tripId) {
        Trip trip = tripService.getTripById(tripId);
        if (trip.getNote() == null){
            return new NoteResponse("");
        }
        return new NoteResponse(trip.getNote().getNote());
    }

    public void createNewNoteForTrip(NoteForTripRequest noteRequest) {
        Trip trip = tripService.getTripById(noteRequest.getTripId());
        Note note;
        if (trip.getNote() != null) {
            note = trip.getNote();
            note.setNote(noteRequest.getNote());
        } else {
            note = Note.builder()
                    .note(noteRequest.getNote())
                    .build();
            trip.setNote(note);
        }
        saveNote(note);
    }
}
