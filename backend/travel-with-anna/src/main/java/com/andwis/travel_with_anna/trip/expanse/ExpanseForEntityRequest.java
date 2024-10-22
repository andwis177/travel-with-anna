package com.andwis.travel_with_anna.trip.expanse;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

@Getter
@Setter
@Builder
public class ExpanseForEntityRequest {
    @NotNull
    private Long entityId;
    @NotBlank(message = "Entity Type is required")
    @NotEmpty(message = "Entity Type is required")
    private String entityType;
}
