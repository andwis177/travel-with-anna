package com.andwis.travel_with_anna.scheduler;

import com.andwis.travel_with_anna.trip.expanse.ExpanseService;
import com.andwis.travel_with_anna.user.token.TokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class SchedulerMng {
    private final TokenService tokenService;
    private final ExpanseService expanseService;

    @Scheduled(cron = "0 30 6 * * ?", zone="Europe/Warsaw")
    public void cleanTokens() {
        tokenService.deleteExpiredTokens();
        log.info("Tokens cleaned.");
    }

    @Scheduled(cron = "0 0 */12 * * ?", zone="Europe/Warsaw")
    public void updateExchangeRates() {
        expanseService.saveAllCurrencyExchange();
        log.info("ExchangeRates updated.");
    }
}
