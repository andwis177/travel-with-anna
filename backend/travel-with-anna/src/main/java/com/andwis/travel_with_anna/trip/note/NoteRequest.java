package com.andwis.travel_with_anna.trip.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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
}
