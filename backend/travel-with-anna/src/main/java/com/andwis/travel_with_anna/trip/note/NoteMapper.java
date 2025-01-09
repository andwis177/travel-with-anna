package com.andwis.travel_with_anna.trip.note;

import org.jetbrains.annotations.NotNull;

public class NoteMapper {

    public static @NotNull NoteResponse toNoteResponse(@NotNull Note note) {
        return new NoteResponse(
                note.getNoteId(),
                note.getContent()
        );
    }
}
