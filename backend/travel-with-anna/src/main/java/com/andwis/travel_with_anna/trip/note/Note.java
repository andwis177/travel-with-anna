package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "notes")
public class Note implements OwnByUser {

    protected static final int NOTE_LENGTH = 500;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Long noteId;

    @Size(max = NOTE_LENGTH)
    @EqualsAndHashCode.Include
    @Column(name = "note", length = NOTE_LENGTH)
    private String content;

    @OneToOne(mappedBy = "note")
    private Day day;

    @OneToOne(mappedBy = "note")
    private Activity activity;

    public void removeActivity() {
        this.activity = null;
    }

    public void removeDay(@NonNull Day day) {
        if (day.getNote() == this) {
            this.day = null;
        }
    }

    @Override
    public User getOwner() {
        return Objects.requireNonNull(this.day, "Note must be linked to a Day to determine an owner.")
                .getOwner();
    }
}
