package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.note.Note;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "trips")
public class Trip extends BaseEntity {

    @Column(name = "trip_name")
    private String tripName;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Day> days;

    @ManyToMany
    @JoinTable(
            name = "trip_viewer",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "viewer_id")
    )
    private Set<User> viewers;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Backpack> backpack;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "note_id")
    private Note note;

    public void addViewer(User user) {
        viewers.add(user);
    }

    public void addDay(Day day) {
        days.add(day);
    }

    public void addBackpack(Backpack backpackItem) {
        this.backpack.add(backpackItem);
    }

    public void removeViewer(User user) {
        viewers.remove(user);
    }

    public void removeDay(Day day) {
        this.days.remove(day);
    }

    public void removeBackpack(Backpack backpackItem) {
        this.backpack.remove(backpackItem);
    }
}
