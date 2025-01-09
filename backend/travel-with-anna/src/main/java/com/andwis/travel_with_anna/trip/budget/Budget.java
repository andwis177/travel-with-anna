package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.User;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "budget")
public class Budget implements OwnByUser {

    private static final int MAX_CURRENCY_LENGTH = 10;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long budgetId;

    @EqualsAndHashCode.Include
    @NotNull
    @Size(max = MAX_CURRENCY_LENGTH)
    @Column(name = "currency", length = MAX_CURRENCY_LENGTH)
    private String currency;

    @EqualsAndHashCode.Include
    @NotNull
    @Column(name = "budget_amount")
    private BigDecimal budgetAmount;

    @OneToOne(mappedBy = "budget")
    @JsonIgnore
    private Trip trip;

    @Override
    public User getOwner() {
        return Objects.requireNonNull(this.trip, "Backpack must be linked to a trip to have an owner")
                .getOwner();
    }
}
