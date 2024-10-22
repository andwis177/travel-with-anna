package com.andwis.travel_with_anna.trip.note;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.function.BiConsumer;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;

    public void saveNote(Note note) {
        noteRepository.save(note);
    }

    public boolean isNoteExists(Long noteId) {
        return noteRepository.existsById(noteId);
    }

    public <T> NoteResponse getNoteById(
            Long entityId,
            @NotNull Function<Long, T> getByIdFunction,
            @NotNull Function<T, Note> getNoteFunction)
    {
        T entity = getByIdFunction.apply(entityId);

        Note note = getNoteFunction.apply(entity);
        if (note == null) {
            return new NoteResponse(-1L, "");
        }
        return new NoteResponse(note.getNoteId(), note.getNote());
    }

    public <T> void saveNote(
            @NotNull NoteRequest noteRequest,
            @NotNull Function<Long, T> getByIdFunction,
            @NotNull Function<T, Note> getNoteFunction,
            @NotNull BiConsumer<T, Note> addNoteFunction
    ) {
        T entity = getByIdFunction.apply(noteRequest.getEntityId());
        Note note = getNoteFunction.apply(entity);
        if (note == null) {
            note = Note.builder()
                    .note(noteRequest.getNote())
                    .build();
            addNoteFunction.accept(entity, note);
        } else {
            note.setNote(noteRequest.getNote());
        }
        saveNote(note);
    }
}
