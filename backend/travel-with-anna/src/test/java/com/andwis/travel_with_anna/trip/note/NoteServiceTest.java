package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NoteServiceTest {
    @Mock
    private NoteRepository noteRepository;

    @Mock
    private TripService tripService;

    @InjectMocks
    private NoteService noteService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveNote() {
        // Given
        Note note = Note.builder().note("Sample note").build();

        // When
        noteService.saveNote(note);

        // Then
        verify(noteRepository, times(1)).save(note);
    }

    @Test
    void testIsNoteExists_True() {
        // Given
        Long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(true);

        // When
        boolean result = noteService.isNoteExists(noteId);

        // Then
        assertTrue(result);
        verify(noteRepository, times(1)).existsById(noteId);
    }

    @Test
    void testIsNoteExists_False() {
        // Given
        Long noteId = 1L;
        when(noteRepository.existsById(noteId)).thenReturn(false);

        // When
        boolean result = noteService.isNoteExists(noteId);

        // Then
        assertFalse(result);
        verify(noteRepository, times(1)).existsById(noteId);
    }

    @Test
    void testGetNoteById_NoNote() {
        // Given
        Long tripId = 1L;
        Trip trip = new Trip();
        when(tripService.getTripById(tripId)).thenReturn(trip);

        // When
        NoteResponse noteResponse = noteService.getNoteById(tripId);

        // Then
        assertNotNull(noteResponse);
        assertEquals("", noteResponse.note());
        verify(tripService, times(1)).getTripById(tripId);
    }

    @Test
    void testGetNoteById_WithNote() {
        // Given
        Long tripId = 1L;
        Note note = Note.builder().note("Sample note").build();
        Trip trip = new Trip();
        trip.setNote(note);
        when(tripService.getTripById(tripId)).thenReturn(trip);

        // When
        NoteResponse noteResponse = noteService.getNoteById(tripId);

        // Then
        assertNotNull(noteResponse);
        assertEquals("Sample note", noteResponse.note());
        verify(tripService, times(1)).getTripById(tripId);
    }

    @Test
    void testCreateNewNoteForTrip_CreatesNewNote() {
        // Given
        Long tripId = 1L;
        NoteForTripRequest request = NoteForTripRequest.builder()
                .tripId(tripId)
                .note("New note")
                .build();
        Trip trip = new Trip();
        when(tripService.getTripById(tripId)).thenReturn(trip);

        // When
        noteService.createNewNoteForTrip(request);

        // Then
        verify(noteRepository, times(1)).save(any(Note.class));
        assertEquals("New note", trip.getNote().getNote());
    }

    @Test
    void testCreateNewNoteForTrip_UpdatesExistingNote() {
        // Given
        Long tripId = 1L;
        Note existingNote = Note.builder().note("Old note").build();
        Trip trip = new Trip();
        trip.setNote(existingNote);
        NoteForTripRequest request = NoteForTripRequest.builder()
                .tripId(tripId)
                .note("Updated note")
                .build();
        when(tripService.getTripById(tripId)).thenReturn(trip);

        // When
        noteService.createNewNoteForTrip(request);

        // Then
        verify(noteRepository, times(1)).save(existingNote);
        assertEquals("Updated note", existingNote.getNote());
    }
}