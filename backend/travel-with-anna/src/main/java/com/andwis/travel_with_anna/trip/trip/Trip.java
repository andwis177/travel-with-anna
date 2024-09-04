package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.HashSet;
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

    @Size(max = 100)
    @Column(name = "trip_name", length = 100)
    private String tripName;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Day> days = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "trip_viewer",
            joinColumns = @JoinColumn(name = "trip_id"),
            inverseJoinColumns = @JoinColumn(name = "viewer_id")
    )
    private Set<User> viewers = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "backpack_id")
    private Backpack backpack;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    public void addViewer(User user) {
        viewers.add(user);
        user.addTripToView(this);
    }

    public void removeViewer(User user) {
        viewers.remove(user);
        user.removeTripToView(this);
    }

    public void setBackpack(Backpack backpack) {
        this.backpack = backpack;
        backpack.setTrip(this);
    }

    public void setBudget(Budget budget) {
        this.budget = budget;
        budget.setTrip(this);
    }

    public void addDay(Day day) {
        days.add(day);
        day.setTrip(this);
    }

    public void removeDay(Day day) {
        this.days.remove(day);
        day.setTrip(null);
    }
}
