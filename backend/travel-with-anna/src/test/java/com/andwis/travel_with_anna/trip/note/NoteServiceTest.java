package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.trip.day.Day;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@DisplayName("Note Service Tests")
class NoteServiceTest {
    @Mock
    private NoteRepository noteRepository;
    private NoteService noteService;
    private NoteRequest noteRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noteService = new NoteService(noteRepository);
        noteRequest = NoteRequest.builder()
                .entityId(1L)
                .note("Note content")
                .build();
    }

    @Test
    void testSaveNote() {
        // Given
        Note note = new Note();
        note.setNoteId(1L);
        note.setNote("Test Note");

        // When
        noteService.saveNote(note);

        // Then
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void testIsNoteExists() {
        // Given
        Long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(true);

        // When
        boolean exists = noteService.isNoteExists(noteId);

        // Then
        assertTrue(exists);
        verify(noteRepository, times(1)).existsById(noteId);
    }

    @Test
    void testGetNoteById_ReturnsValidNoteResponse() {
        // Given
        Long entityId = 1L;
        Note note = new Note();
        note.setNoteId(entityId);
        note.setNote("Test Note");
        Function<Long, Note> getByIdFunction = id -> note;
        Function<Note, Note> getNoteFunction = Function.identity();

        // When
        NoteResponse response = noteService.getNoteById(entityId, getByIdFunction, getNoteFunction);

        // Then
        assertEquals(entityId, response.noteId());
        assertEquals("Test Note", response.note());
    }

    @Test
    void testGetNoteById_ReturnsDefaultNoteResponse_WhenNoteIsNull() {
        // Given
        Long entityId = 1L;
        Function<Long, Note> getByIdFunction = id -> null;
        Function<Note, Note> getNoteFunction = Function.identity();

        // When
        NoteResponse response = noteService.getNoteById(entityId, getByIdFunction, getNoteFunction);

        // Then
        assertEquals(-1L, response.noteId());
        assertEquals("", response.note());
    }

    @Test
    @Transactional
    void testSaveNoteWithNoteRequest_NewNote() {
        // Given
        Function<Long, Day> getByIdFunction = id -> new Day();
        Function<Day, Note> getNoteFunction = day -> null;
        BiConsumer<Day, Note> addNoteFunction = (day, note) -> {};
        Consumer<Day> removeNoteFunction = day -> {};

        // When
        noteService.saveNote(noteRequest, getByIdFunction, getNoteFunction, addNoteFunction, removeNoteFunction);

        // Then
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository, times(1)).save(noteCaptor.capture());
        Note savedNote = noteCaptor.getValue();
        assertEquals("Note content", savedNote.getNote());
    }

    @Test
    @Transactional
    void testSaveNoteWithNoteRequest_ExistingNote() {
        // Given
        Long noteId = 1L;
        Note existingNote = new Note();
        existingNote.setNoteId(noteId);
        existingNote.setNote("Existing Note");

        Function<Long, Day> getByIdFunction = id -> new Day();
        Function<Day, Note> getNoteFunction = day -> existingNote;
        BiConsumer<Day, Note> addNoteFunction = (day, note) -> {};
        Consumer<Day> removeNoteFunction = day -> {};

        // When
        noteService.saveNote(noteRequest, getByIdFunction, getNoteFunction, addNoteFunction, removeNoteFunction);

        // Then
        verify(noteRepository, times(1)).save(existingNote);
        assertEquals("Note content", existingNote.getNote());
    }

    @Test
    @Transactional
    void testSaveNoteWithNoteRequest_DeleteNote() {
        // Given
        Long noteId = 1L;
        Note existingNote = new Note();
        existingNote.setNoteId(noteId);
        existingNote.setNote("Existing Note");

        Function<Long, Day> getByIdFunction = id -> new Day();
        Function<Day, Note> getNoteFunction = day -> existingNote;
        BiConsumer<Day, Note> addNoteFunction = (day, note) -> {};
        Consumer<Day> removeNoteFunction = day -> {};

        noteRequest.setNote("");

        // When
        noteService.saveNote(noteRequest, getByIdFunction, getNoteFunction, addNoteFunction, removeNoteFunction);

        // Then
        verify(noteRepository, times(1)).delete(existingNote);
    }
}