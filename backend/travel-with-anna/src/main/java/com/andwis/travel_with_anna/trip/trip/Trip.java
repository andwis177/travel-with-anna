package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.security.OwnByUser;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "trips")

public class Trip implements OwnByUser {

    protected final static int TRIP_NAME_LENGTH = 60;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trip_id")
    private Long tripId;

    @EqualsAndHashCode.Include
    @Size(max = TRIP_NAME_LENGTH)
    @Column(name = "trip_name", length = TRIP_NAME_LENGTH)
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

    public void addDays(@NotNull Collection<Day> days) {
        for (Day day : days) {
            addDay(day);
        }
    }

    public void replaceDays(@NotNull List<Day> newDays) {
        this.days.clear();
        for (Day day : newDays) {
            addDay(day);
        }
    }

    public void addExpanse(Expanse expanse) {
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
        if (this.backpack != null) this.backpack.setTrip(null);
        if (this.budget != null) this.budget.setTrip(null);
        if (this.owner != null) this.owner.removeTrip(this);

    }

    public List<Day> getDaysInOrder() {
        return days.stream()
                .sorted(Comparator.comparing(Day::getDate))
                .toList();
    }
}
