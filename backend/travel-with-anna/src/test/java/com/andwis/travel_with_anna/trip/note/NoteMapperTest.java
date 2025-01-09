package com.andwis.travel_with_anna.trip.note;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NoteMapperTest {
    private Note note;

    @BeforeEach
    void setUp() {

        note = new Note();
        note.setNoteId(1L);
        note.setContent("Test Note Content");
    }

    @Test
    void testToNoteResponse_createsNoteResponse() {
        // Given
        note.setNoteId(1L);
        note.setContent("Test Note Content");

        // When
        NoteResponse response = NoteMapper.toNoteResponse(note);

        // Then
        assertNotNull(response);
        assertEquals(note.getNoteId(), response.noteId());
        assertEquals(note.getContent(), response.note());
    }
}