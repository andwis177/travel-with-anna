package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.trip.trip.Trip;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "days")
public class Day {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long dayId;

    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Activity> activity;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return Objects.equals(dayId, day.dayId) && Objects.equals(date, day.date) && Objects.equals(note, day.note) && Objects.equals(trip, day.trip) && Objects.equals(activity, day.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayId, date);
    }
}
