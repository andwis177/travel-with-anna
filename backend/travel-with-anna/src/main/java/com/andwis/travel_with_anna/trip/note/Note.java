package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "notes")
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long noteId;

    @Size(max = 500)
    @Column(name = "note", length = 500)
    private String note;

    @OneToOne(mappedBy = "note")
    private Day day;

    @OneToOne(mappedBy = "note")
    private Activity activity;

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Note objectNote = (Note) object;
        return Objects.equals(noteId, objectNote.noteId)
                && Objects.equals(note, objectNote.note)
                && Objects.equals(day, objectNote.day)
                && Objects.equals(activity, objectNote.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, note);
    }
}
