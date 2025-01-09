package com.andwis.travel_with_anna.trip.day;

import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.User;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "days")
public class Day implements OwnByUser {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long dayId;

    @EqualsAndHashCode.Include
    @NotNull
    @Column(name = "date")
    private LocalDate date;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Activity> activities = new HashSet<>();

    public void addActivity(@NotNull Activity activity) {
        this.activities.add(activity);
        activity.setDay(this);
    }

    public void removeActivities(Set<Activity> activities) {
        activities.forEach(activity -> activity.setDay(null));
        this.activities.removeAll(activities);
    }

    public void addNote(Note note) {
        this.note = note;
        note.setDay(this);
    }

    public void removeNote() {
        if (this.note != null) {
            this.note.removeDay(this);
            this.note = null;
        }
    }

    @Override
    public User getOwner() {
        return Objects.requireNonNull(this.trip, "Day must be linked to a trip to have an owner")
                .getOwner();
    }
}
