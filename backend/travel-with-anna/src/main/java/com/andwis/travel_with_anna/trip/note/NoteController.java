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

    @PostMapping("/trip/save")
    public ResponseEntity<Void> saveNoteForTrip(@RequestBody @Valid NoteRequest request) {
        facade.saveNoteForTrip(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/activity/save")
    public ResponseEntity<Void> saveNoteForActivity(@RequestBody @Valid NoteRequest request) {
        facade.saveNoteForActivity(request);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("trip/{tripId}")
    public ResponseEntity<NoteResponse> getNoteByTripId(@PathVariable("tripId") Long tripId) {
        return ResponseEntity.ok(facade.getNoteByTripId(tripId));
    }

    @GetMapping("activity/{activityId}")
    public ResponseEntity<NoteResponse> getNoteByActivityId(@PathVariable("activityId") Long activityId) {
        return ResponseEntity.ok(facade.getNoteByActivityId(activityId));
    }
}
