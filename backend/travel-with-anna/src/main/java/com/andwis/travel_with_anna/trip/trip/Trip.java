package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.Objects;

;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trips")
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long tripId;

    @Size(max = 100)
    @Column(name = "trip_name", length = 100)
    private String tripName;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Day> days;


    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "backpack_id")
    private Backpack backpack;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Expanse> expanses;

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

    public void addExpanse(Expanse expanse) {
        this.expanses.add(expanse);
        expanse.setTrip(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(tripId, trip.tripId) && Objects.equals(tripName, trip.tripName) && Objects.equals(owner, trip.owner) && Objects.equals(days, trip.days) && Objects.equals(backpack, trip.backpack) && Objects.equals(budget, trip.budget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, tripName);
    }
}
