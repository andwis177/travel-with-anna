package com.andwis.travel_with_anna.trip.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

import static com.andwis.travel_with_anna.trip.note.Note.NOTE_LENGTH;

@Getter
@Setter
@Builder
public class NoteRequest {

        private static final String ENTITY_TYPE_REQUIRED_MESSAGE = "Entity type is required.";

        @Size(max = NOTE_LENGTH, message = "Note name should be " + NOTE_LENGTH + " characters or less")
        private String noteContent;

        private Long noteId;

        private Long linkedEntityId;

        @NotEmpty(message = ENTITY_TYPE_REQUIRED_MESSAGE)
        @NotBlank(message = ENTITY_TYPE_REQUIRED_MESSAGE)
        private String linkedEntityType;

        @Override
        public boolean equals(Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                NoteRequest that = (NoteRequest) object;
                return Objects.equals(noteContent, that.noteContent) &&
                        Objects.equals(noteId, that.noteId);
        }

        @Override
        public int hashCode() {
                return Objects.hash(noteContent, noteId);
        }
}
