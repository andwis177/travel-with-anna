package com.andwis.travel_with_anna.api.currency;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "currency_exchange")
public class CurrencyExchange {

    public static final int EXCHANGE_VALUE_SCALE = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "exchange_value", precision = 20, scale = EXCHANGE_VALUE_SCALE)
    private BigDecimal exchangeValue;

    @NotNull
    @Column(name = "date", nullable = false)
    private LocalDateTime timestamp;
}
