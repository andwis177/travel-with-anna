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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "currency_id")
    private Long currencyId;

    @Column(name = "code")
    private String code;

    @Column(name = "exchange_value")
    private BigDecimal exchangeValue ;

    @NotNull
    @Column(name = "date")
    private LocalDateTime timeStamp;
}
