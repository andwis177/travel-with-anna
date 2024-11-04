package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.trip.trip.Trip;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private Set<Activity> activities;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Day day = (Day) o;
        return Objects.equals(dayId, day.dayId)
                && Objects.equals(date, day.date)
                && Objects.equals(note, day.note)
                && Objects.equals(trip, day.trip)
                && Objects.equals(activities, day.activities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayId, date);
    }

    public void addActivity(Activity activity) {
        if (this.activities == null) {
            this.activities = new HashSet<>();
        }
        this.activities.add(activity);
        activity.setDay(this);
    }

    public void removeActivities(Set<Activity> activities) {
        activities.forEach(activity -> activity.setDay(null));
        this.activities.removeAll(activities);

    }

    public void addNote (Note note) {
        this.note = note;
        note.setDay(this);
    }

    public void removeNote() {
        if (this.note != null) {
            this.note.removeDay(this);
            this.note = null;
        }
    }
}
