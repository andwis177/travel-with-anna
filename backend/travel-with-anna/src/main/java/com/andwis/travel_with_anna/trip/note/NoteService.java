package com.andwis.travel_with_anna.trip.note;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class NoteService {

    private static final NoteResponse MISSING_NOTE_RESPONSE = new NoteResponse(-1L, "");

    private final NoteRepository noteRepository;

    public void saveNoteEntity(Note note) {
        noteRepository.save(note);
    }

    public boolean isNoteExists(Long noteId) {
        return noteRepository.existsById(noteId);
    }

    public <T> NoteResponse getNoteById(
            Long entityId,
            @NotNull Function<Long, T> getByIdFunction,
            @NotNull Function<T, Note> getNoteFunction,
            @NotNull BiConsumer<T, UserDetails> userAuthorizationFunction,
            UserDetails connectedUser
    )
    {
        T entity = getByIdFunction.apply(entityId);
        userAuthorizationFunction.accept(entity, connectedUser);

        Note note = getNoteFunction.apply(entity);

        return (note == null)
                ? MISSING_NOTE_RESPONSE
                : new NoteResponse(note.getNoteId(), note.getContent());
    }

    @Transactional
    public <T> void saveNote(
            @NotNull NoteRequest noteRequest,
            @NotNull Function<Long, T> getByIdFunction,
            @NotNull Function<T, Note> getNoteFunction,
            @NotNull BiConsumer<T, Note> addNoteFunction,
            @NotNull Consumer<T> removeNoteFunction,
            @NotNull BiConsumer<T, UserDetails> userAuthorizationFunction,
            UserDetails connectedUser
    ) {
        T entity = getByIdFunction.apply(noteRequest.getLinkedEntityId());
        userAuthorizationFunction.accept(entity, connectedUser);

        Note note = getNoteFunction.apply(entity);

        if (note == null) {
            createAndSaveNewNote(noteRequest, entity, addNoteFunction);
        } else {
            updateOrDeleteNote(noteRequest, entity, note, removeNoteFunction);
        }
    }

    private <T> void createAndSaveNewNote(
            @NotNull NoteRequest noteRequest,
            T entity,
            @NotNull BiConsumer<T, Note> addNoteFunction
    ) {
        Note newNote = Note.builder()
                .content(noteRequest.getNoteContent())
                .build();

        addNoteFunction.accept(entity, newNote);
        saveNoteEntity(newNote);
    }

    private <T> void updateOrDeleteNote(
            @NotNull NoteRequest noteRequest,
            T entity,
            Note existingNote,
            @NotNull Consumer<T> removeNoteFunction
    ) {
        String noteContent = noteRequest.getNoteContent();

        if (noteContent == null || noteContent.isBlank()) {
            removeNoteFunction.accept(entity);
            deleteNote(existingNote);
            return;
        }

        existingNote.setContent(noteContent);
        saveNoteEntity(existingNote);
    }

    private void deleteNote(Note note) {
        noteRepository.delete(note);
    }
}
