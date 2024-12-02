package com.andwis.travel_with_anna.trip.note;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("note")
@Tag(name = "Note")
public class NoteController {
    private final NoteFacade facade;

    @PostMapping
    public ResponseEntity<Void> saveNote(
            @RequestBody @Valid NoteRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        facade.saveNote(request, connectedUser);
        return ResponseEntity.accepted().build();
    }

    @GetMapping
    public ResponseEntity<NoteResponse> getNote(
            @RequestParam("entityId") Long entityId,
            @RequestParam("entityType") String entityType,
            @AuthenticationPrincipal UserDetails connectedUser) {
        NoteResponse response = facade.getNote(entityId, entityType, connectedUser);
        return ResponseEntity.ok(response);
    }
}
