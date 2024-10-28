package com.andwis.travel_with_anna.trip.note;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("note")
@Tag(name = "Note")
public class NoteController {
    private final NoteFacade facade;

    @PostMapping("/save")
    public ResponseEntity<Void> saveNote(@RequestBody @Valid NoteRequest request) {
        facade.saveNote(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("")
    public ResponseEntity<NoteResponse> getNote(
            @RequestParam("entityId") Long entityId, @RequestParam("entityType") String entityType) {
        NoteResponse response = facade.getNote(entityId, entityType);
        return ResponseEntity.ok(response);
    }
}
