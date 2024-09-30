package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.trip.trip.Trip;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "budget")
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long budgetId;

    @Size(max = 10)
    @Column(name = "currency", length = 10)
    private String currency;

    @NotNull
    @Column(name = "to_spend")
    private BigDecimal toSpend;

    @OneToOne(mappedBy = "budget")
    @JsonIgnore
    private Trip trip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Budget budget = (Budget) o;
        return Objects.equals(budgetId, budget.budgetId)
                && Objects.equals(currency, budget.currency)
                && Objects.equals(toSpend, budget.toSpend)
                && Objects.equals(trip, budget.trip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(budgetId, currency, toSpend);
    }
}
