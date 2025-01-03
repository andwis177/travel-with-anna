package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.security.OwnableByUser;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.expanse.Expanse;
import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "trips")
public class Trip implements OwnableByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long tripId;

    @Size(max = 60)
    @Column(name = "trip_name", length = 60)
    private String tripName;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Day> days = new HashSet<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "backpack_id")
    private Backpack backpack;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "budget_id")
    private Budget budget;

    @Builder.Default
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "trip", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Expanse> expanses = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(tripId, trip.tripId)
                && Objects.equals(tripName, trip.tripName)
                && Objects.equals(owner, trip.owner)
                && Objects.equals(days, trip.days)
                && Objects.equals(backpack, trip.backpack)
                && Objects.equals(budget, trip.budget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tripId, tripName);
    }

    public void addBackpack(@NotNull Backpack backpack) {
        this.backpack = backpack;
        backpack.setTrip(this);
    }

    public void addBudget(@NotNull Budget budget) {
        this.budget = budget;
        budget.setTrip(this);
    }

    public void addDay(Day day) {
        days.add(day);
        day.setTrip(this);
    }

    public void addDays(List<Day> days) {
        this.days.clear();
        this.days.addAll(days);
        for (Day day : days) {
            day.setTrip(this);
        }
    }

    public void addExpanse(Expanse expanse) {
        if (this.expanses == null) {
            this.expanses = new HashSet<>();
        }
        this.expanses.add(expanse);
        expanse.setTrip(this);
    }

    public void removeTripAssociations() {
        if (this.days != null) {
            for (Day day : this.days) {
                day.setTrip(null);
            }
        }
        if (this.expanses != null) {
            for (Expanse expanse : this.expanses) {
                expanse.setTrip(null);
            }
        }
        if (this.backpack != null) {
            this.backpack.setTrip(null);
        }
        if (this.budget != null) {
            this.budget.setTrip(null);
        }
        if (this.owner != null) {
            this.owner.removeTrip(this);
        }
    }

    public List<Day> getDaysInOrder() {
        List<Day> daysList = new ArrayList<>(days);
        daysList.sort(Comparator.comparing(Day::getDate));
        return daysList;
    }
}
