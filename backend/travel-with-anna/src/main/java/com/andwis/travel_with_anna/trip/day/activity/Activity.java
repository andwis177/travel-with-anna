package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.note.Note;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalTime;
import java.util.Objects;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.JOINED)
@Entity
@Table(name = "activity")
public abstract class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "activity_id")
    private Long activityId;

    @Column(name ="time")
    private LocalTime time;

    @Size(max = 40)
    @NotNull
    @Column(name = "badge" , length = 40)
    private String badge;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private Day day;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expanse_id")
    private Expanse expanse;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Activity activity = (Activity) o;
        return Objects.equals(activityId, activity.activityId)
                && Objects.equals(badge, activity.badge)
                && Objects.equals(note, activity.note)
                && Objects.equals(day, activity.day)
                && Objects.equals(expanse, activity.expanse);
    }

    @Override
    public int hashCode() {
        return Objects.hash(activityId);
    }
}
