package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.security.OwnByUser;
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
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity
@Table(name = "expanses")
public class Expanse implements OwnByUser {

    public static final int MAX_EXPANSE_NAME_LENGTH = 60;
    public static final int MAX_CURRENCY_LENGTH = 10;
    public static final int MAX_CATEGORY_LENGTH = 255;

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expanse_id")
    private Long expanseId;

    @EqualsAndHashCode.Include
    @Size(max = MAX_EXPANSE_NAME_LENGTH)
    @Column(name = "expanse_name", length = MAX_EXPANSE_NAME_LENGTH)
    private String expanseName;

    @Size(max = MAX_CATEGORY_LENGTH)
    @Column(name = "expanse_category")
    private String expanseCategory;

    @EqualsAndHashCode.Include
    @Column(name = "date")
    private LocalDate date;

    @EqualsAndHashCode.Include
    @NotNull
    @Size(max = MAX_CURRENCY_LENGTH)
    @Column(name = "currency", length = MAX_CURRENCY_LENGTH)
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

    public BigDecimal calculatePriceInTripCurrency() {
        return calculateCurrencyValue(this.price);
    }

    public BigDecimal calculatePaidInTripCurrency() {
        return calculateCurrencyValue(this.paid);
    }

    private BigDecimal calculateCurrencyValue(BigDecimal amount) {
        if (this.exchangeRate == null || amount == null) {
            return BigDecimal.ZERO;
        }
        return this.exchangeRate.multiply(amount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public User getOwner() {
        return Objects.requireNonNull(this.trip, "Backpack must be linked to a trip to have an owner")
                .getOwner();
    }
}
