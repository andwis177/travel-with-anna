package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("Note Service Tests")
class NoteServiceTest {
    @Mock
    private NoteRepository noteRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private NoteService noteService;
    private NoteRequest noteRequest;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        noteService = new NoteService(noteRepository);
        noteRequest = NoteRequest.builder()
                .linkedEntityId(1L)
                .noteContent("Note content")
                .build();

        Role role = new Role();
        role.setRoleName(USER.getRoleName());
        role.setRoleAuthority(USER.getAuthority());

        String encodedPassword = passwordEncoder.encode("password");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);

        userDetails = createUserDetails(user);
    }

    @Test
    void testSaveNote() {
        // Given
        Note note = new Note();
        note.setNoteId(1L);
        note.setContent("Test Note");

        // When
        noteService.saveNoteEntity(note);

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
        note.setContent("Test Note");
        Function<Long, Note> getByIdFunction = id -> note;
        Function<Note, Note> getNoteFunction = Function.identity();

        // When
        NoteResponse response = noteService.getNoteById(
                entityId, getByIdFunction, getNoteFunction, (id, userDetails) -> {}, userDetails);

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
        NoteResponse response = noteService.getNoteById(
                entityId, getByIdFunction, getNoteFunction, (id, userDetails) -> {}, userDetails);

        // Then
        assertEquals(-1L, response.noteId());
        assertEquals("", response.note());
    }

    @Test
    @Transactional
    void testSaveNoteWithNoteRequest_NewNote() {
        // Given
        Function<Long, Day> getByIdFunction = id -> new Day();
        Function<Day, Note> getNoteFunction = dayId -> null;
        BiConsumer<Day, Note> addNoteFunction = (dayId, noteId) -> {};
        Consumer<Day> removeNoteFunction = dayId -> {};

        // When
        noteService.saveNote(noteRequest, getByIdFunction, getNoteFunction, addNoteFunction, removeNoteFunction,
                (dayId, userDetails) -> {}, userDetails);

        // Then
        ArgumentCaptor<Note> noteCaptor = ArgumentCaptor.forClass(Note.class);
        verify(noteRepository, times(1)).save(noteCaptor.capture());
        Note savedNote = noteCaptor.getValue();
        assertEquals("Note content", savedNote.getContent());
    }

    @Test
    @Transactional
    void testSaveNoteWithNoteRequest_ExistingNote() {
        // Given
        Long noteId = 1L;
        Note existingNote = new Note();
        existingNote.setNoteId(noteId);
        existingNote.setContent("Existing Note");

        Function<Long, Day> getByIdFunction = id -> new Day();
        Function<Day, Note> getNoteFunction = dayId -> existingNote;
        BiConsumer<Day, Note> addNoteFunction = (dayId, note_Id) -> {};
        Consumer<Day> removeNoteFunction = dayId -> {};

        // When
        noteService.saveNote(noteRequest, getByIdFunction, getNoteFunction, addNoteFunction, removeNoteFunction,
                (dayId, userDetails) -> {}, userDetails);

        // Then
        verify(noteRepository, times(1)).save(existingNote);
        assertEquals("Note content", existingNote.getContent());
    }

    @Test
    @Transactional
    void testSaveNoteWithNoteRequest_DeleteNote() {
        // Given
        Long noteId = 1L;
        Note existingNote = new Note();
        existingNote.setNoteId(noteId);
        existingNote.setContent("Existing Note");

        Function<Long, Day> getByIdFunction = id -> new Day();
        Function<Day, Note> getNoteFunction = dayId -> existingNote;
        BiConsumer<Day, Note> addNoteFunction = (dayId, note_Id) -> {};
        Consumer<Day> removeNoteFunction = dayId -> {};

        noteRequest.setNoteContent("");

        // When
        noteService.saveNote(noteRequest, getByIdFunction, getNoteFunction, addNoteFunction, removeNoteFunction,
                (dayId, userDetails) -> {}, userDetails);

        // Then
        verify(noteRepository, times(1)).delete(existingNote);
    }

    private @NotNull UserDetails createUserDetails(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        securityUser,
                        user.getPassword(),
                        securityUser.getAuthorities()
                )
        );
        return securityUser;
    }
}