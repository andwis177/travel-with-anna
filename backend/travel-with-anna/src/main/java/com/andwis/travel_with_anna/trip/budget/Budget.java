package com.andwis.travel_with_anna.trip.budget;

import com.andwis.travel_with_anna.utility.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "budget")
public class Budget extends BaseEntity {

    @NotNull
    @Column(name = "currency", length = 10)
    private String currency;

    @NotNull
    @Column(name = "to_spend")
    private BigDecimal toSpend;

}
