package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.Address;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.trip.note.Note;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@EqualsAndHashCode()
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "activities")
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activity_id")
    private Long activityId;

    @Size(max = 60)
    @Column(name = "activity_title", length = 60)
    private String activityTitle;

    @Column(name ="begin_time")
    private LocalTime beginTime;

    @Column(name ="end_time")
    private LocalTime endTime;

    @Size(max = 20)
    @Column(name = "badge" , length = 20)
    private String badge;

    @Size(max = 20)
    @Column(name = "type", length = 20)
    private String type;

    @Size(max = 20)
    @Column(name = "status", length = 20)
    private String status;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne
    @JoinColumn(name = "day_id")
    private Day day;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "expanse_id")
    private Expanse expanse;

    @Column(name = "associated_id")
    private Long associatedId;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;

    public String getBeginTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return beginTime != null ? this.beginTime.format(formatter) : "";
    }

    public String getEndTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        return  associatedId == null && endTime != null ? this.endTime.format(formatter) : "";
    }

    public void addNote(Note note) {
        this.note = note;
        note.setActivity(this);
    }

    public void addExpanse(Expanse expanse) {
        this.expanse = expanse;
        expanse.setActivity(this);
    }
}
