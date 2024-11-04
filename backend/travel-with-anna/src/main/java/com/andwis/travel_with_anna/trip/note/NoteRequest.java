package com.andwis.travel_with_anna.trip.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class NoteRequest {
        @Size(max = 500, message = "Expanse name should be 500 characters or less")
        private String note;
        private Long noteId;
        private Long entityId;
        @NotEmpty(message = "Entity type is required")
        @NotBlank(message = "Entity type is required")
        private String entityType;

        @Override
        public boolean equals(Object object) {
                if (this == object) return true;
                if (object == null || getClass() != object.getClass()) return false;
                NoteRequest that = (NoteRequest) object;
                return Objects.equals(note, that.note) &&
                        Objects.equals(noteId, that.noteId) &&
                        Objects.equals(entityId, that.entityId) &&
                        Objects.equals(entityType, that.entityType);
        }

        @Override
        public int hashCode() {
                return Objects.hash(note, noteId, entityId, entityType);
        }
}
