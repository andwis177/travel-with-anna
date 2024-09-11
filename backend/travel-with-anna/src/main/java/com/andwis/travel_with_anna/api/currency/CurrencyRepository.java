package com.andwis.travel_with_anna.api.currency;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CurrencyRepository extends JpaRepository<CurrencyExchange, Long> {
    Optional<CurrencyExchange> findByCode(String code);

}