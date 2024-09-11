package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.badge.Badge;
import com.andwis.travel_with_anna.trip.day.activity.badge.BadgeProvider;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.note.Note;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "activity")
public abstract class Activity implements BadgeProvider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @ManyToOne
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "day_id")
    @JsonIgnore
    private Day day;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expanse_id")
    private Expanse expanse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(activityId, activity.activityId) && Objects.equals(badge, activity.badge) && Objects.equals(note, activity.note) && Objects.equals(day, activity.day) && Objects.equals(expanse, activity.expanse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId);
    }
}
