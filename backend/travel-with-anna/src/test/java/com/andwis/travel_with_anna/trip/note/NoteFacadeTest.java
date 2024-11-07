package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.handler.exception.NoteTypeException;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Note Facade Tests")
class NoteFacadeTest {
    @InjectMocks
    private NoteFacade noteFacade;
    @Mock
    private NoteService noteService;
    @Mock
    private DayService dayService;
    @Mock
    private ActivityService activityService;
    private NoteRequest noteRequest;

    @BeforeEach
    void setUp() {
        noteRequest = NoteRequest.builder()
                .noteId(1L)
                .entityId(10L)
                .note("Sample note")
                .entityType("day")
                .build();
    }

    @Test
    void testSaveNoteWithValidDayEntity() {
        // Given

        // When
        noteFacade.saveNote(noteRequest);

        // Then
        verify(noteService, times(1)).saveNote(
                eq(noteRequest),
                any(),
                any(),
                any(),
                any());
    }

    @Test
    void testSaveNoteWithValidActivityEntity() {
        // Given
        noteRequest.setEntityType("activity");

        // When
        noteFacade.saveNote(noteRequest);

        // Then
        verify(noteService, times(1)).saveNote(
                eq(noteRequest),
                any(),
                any(),
                any(),
                any());
    }

    @Test
    void testSaveNoteWithInvalidType() {
        // Given
        noteRequest.setEntityType("invalid_type");

        // When
        NoteTypeException exception = assertThrows(NoteTypeException.class, () -> {
            noteFacade.saveNote(noteRequest);
        });

        // Then
        assertTrue(exception.getMessage().contains("Invalid note type"));
    }

    @Test
    void testGetNoteForDayEntity() {
        // Given
        Long dayId = 10L;
        NoteResponse expectedResponse = new NoteResponse(1L, "Sample note");
        when(noteService.getNoteById(eq(dayId), any(), any())).thenReturn(expectedResponse);

        // When
        NoteResponse actualResponse = noteFacade.getNote(dayId, "day");

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.noteId(), actualResponse.noteId());
        assertEquals(expectedResponse.note(), actualResponse.note());
        verify(noteService, times(1)).getNoteById(eq(dayId), any(), any());
    }

    @Test
    void testGetNoteForActivityEntity() {
        // Given
        Long activityId = 20L;
        NoteResponse expectedResponse = new NoteResponse(1L, "Sample note");
        when(noteService.getNoteById(eq(activityId), any(), any())).thenReturn(expectedResponse);

        // When
        NoteResponse actualResponse = noteFacade.getNote(activityId, "activity");

        // Then
        assertNotNull(actualResponse);
        assertEquals(expectedResponse.noteId(), actualResponse.noteId());
        assertEquals(expectedResponse.note(), actualResponse.note());
        verify(noteService, times(1)).getNoteById(eq(activityId), any(), any());
    }

    @Test
    void testGetNoteWithInvalidType() {
        // Given
        Long invalidEntityId = 30L;

        // When
        NoteResponse response = noteFacade.getNote(invalidEntityId, "invalid_type");

        // Then
        assertNotNull(response);
        assertEquals(-1L, response.noteId());
        assertEquals("", response.note());
    }
}