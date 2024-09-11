package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(length = 500)
    private String note;

    @OneToOne(mappedBy = "note")
    @JsonIgnore
    private Day day;

    @OneToOne(mappedBy = "note")
    @JsonIgnore
    private Backpack backpack;

    @OneToOne(mappedBy = "note")
    @JsonIgnore
    private Activity activity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Note note1 = (Note) o;
        return Objects.equals(noteId, note1.noteId) && Objects.equals(note, note1.note) && Objects.equals(day, note1.day) && Objects.equals(backpack, note1.backpack) && Objects.equals(activity, note1.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(noteId, note);
    }
}
