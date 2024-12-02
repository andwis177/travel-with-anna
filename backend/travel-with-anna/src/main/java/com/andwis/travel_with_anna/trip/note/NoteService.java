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
    private final NoteRepository noteRepository;

    public void save(Note note) {
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
        if (note == null) {
            return new NoteResponse(-1L, "");
        }
        return new NoteResponse(note.getNoteId(), note.getNote());
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
        T entity = getByIdFunction.apply(noteRequest.getEntityId());
        userAuthorizationFunction.accept(entity, connectedUser);
        Note note = getNoteFunction.apply(entity);

        if (note == null) {
            note = Note.builder()
                    .note(noteRequest.getNote())
                    .build();
            addNoteFunction.accept(entity, note);
            save(note);
        } else {
            if (noteRequest.getNote() != null) {

                if (!noteRequest.getNote().isEmpty()) {

                    if (!noteRequest.getNote().isBlank()) {
                        note.setNote(noteRequest.getNote());
                        save(note);
                    } else {
                        deleteNote(note);
                    }
                } else
                {
                    removeNoteFunction.accept(entity);
                    deleteNote(note);
                }
            }
        }
    }

    private void deleteNote(Note note) {
        noteRepository.delete(note);
    }
}
