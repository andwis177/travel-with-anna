package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@EqualsAndHashCode()
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "activities")
public class Activity implements OwnByUser, Comparable<Activity> {

    protected static final String TIME_FORMAT = "HH:mm";
    protected static final int MAX_TITLE_LENGTH = 60;
    protected static final int MAX_LENGTH = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Size(max = MAX_TITLE_LENGTH)
    @Column(name = "activity_title", length = MAX_TITLE_LENGTH)
    private String activityTitle;

    @Column(name ="begin_time")
    private LocalTime beginTime;

    @Column(name ="end_time")
    private LocalTime endTime;

    @Size(max = MAX_LENGTH)
    @Column(name = "badge" , length = MAX_LENGTH)
    private String badge;

    @Size(max = MAX_LENGTH)
    @Column(name = "type", length = MAX_LENGTH)
    private String type;

    @Size(max = MAX_LENGTH)
    @Column(name = "status", length = MAX_LENGTH)
    private String status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private Day day;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expanse_id")
    private Expanse expanse;

    @Column(name = "associated_id")
    private Long associatedId;

    @ManyToOne(cascade = CascadeType.ALL )
    @JoinColumn(name = "address_id")
    private Address address;

    @Column(name = "day_tag")
    private boolean dayTag;

    private @NotNull String formatTime(LocalTime time) {
        if (time == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(TIME_FORMAT);
        return time.format(formatter);
    }

    public String getFormattedBeginTime() {
        return formatTime(this.beginTime);
    }

    public String getFormattedEndTime() {
        return this.associatedId == null ? formatTime(this.endTime) : "";
    }

    public void addNote(@NotNull Note note) {
        this.note = note;
        note.setActivity(this);
    }

    public void removeNote() {
        if (this.note != null) {
            this.note.removeActivity();
            this.note = null;
        }
    }

    public void addExpanse(@NotNull Expanse expanse) {
        this.expanse = expanse;
        expanse.setActivity(this);
    }

    public void addAddress(@NotNull Address address) {
        this.address = address;
        address.addLinkedActivity(this);
    }

    @Override
    public User getOwner() {
        return Objects.requireNonNull(this.day, "Backpack must be linked to a day to have an owner")
                .getOwner();
    }

    @Override
    public int compareTo(@NotNull Activity o) {
        return this.beginTime.compareTo(o.beginTime);
    }
}
