package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.security.OwnableByUser;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "expanses")
public class Expanse implements OwnableByUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expanse_id")
    private Long expanseId;

    @Size(max = 60)
    @Column(name = "expanse_name", length = 60)
    private String expanseName;

    @Column(name = "expanse_category")
    private String expanseCategory;

    @Column(name = "date")
    private LocalDate date;

    @NotNull
    @Size(max = 10)
    @Column(name = "currency", length = 10)
    private String currency;

    @NotNull
    @Column(name = "price")
    private BigDecimal price;

    @NotNull
    @Column(name = "paid")
    private BigDecimal paid;

    @NotNull
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @OneToOne(mappedBy = "expanse")
    private Item item;

    @OneToOne(mappedBy = "expanse")
    private Activity activity;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expanse expanse = (Expanse) o;
        return Objects.equals(expanseId, expanse.expanseId) && Objects.equals(expanseName, expanse.expanseName)
                && Objects.equals(currency, expanse.currency) && Objects.equals(price, expanse.price)
                && Objects.equals(paid, expanse.paid) && Objects.equals(exchangeRate, expanse.exchangeRate)
                &&  Objects.equals(activity, expanse.activity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(expanseId, expanseName, currency, price, paid, exchangeRate);
    }

    public BigDecimal getPriceInTripCurrency() {
        if(exchangeRate == null || price == null) {
            return BigDecimal.ZERO;
        }
        return exchangeRate.multiply(price).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getPaidInTripCurrency() {
        if(exchangeRate == null || paid == null) {
            return BigDecimal.ZERO;
        }
        return exchangeRate.multiply(paid).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public User getOwner() {
        return this.trip.getOwner();
    }
}
