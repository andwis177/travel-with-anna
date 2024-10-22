package com.andwis.travel_with_anna.trip.note;

public class NoteMapper {

    public static NoteResponse toNoteResponse(Note note) {
        return new NoteResponse(
                note.getNoteId(),
                note.getNote()
        );
    }
}
