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
    public ResponseEntity<Void> createNewNoteForTrip(@RequestBody @Valid NoteForTripRequest request) {
        facade.createNewNoteForTrip(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<NoteResponse> getNoteById(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok(facade.getNoteById(tripId));
    }
}
