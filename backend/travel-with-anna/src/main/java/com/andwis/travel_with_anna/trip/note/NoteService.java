package com.andwis.travel_with_anna.trip.note;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NoteService {
    private final NoteRepository noteRepository;

    public void saveNote(Note note) {
        noteRepository.save(note);
    }
}
