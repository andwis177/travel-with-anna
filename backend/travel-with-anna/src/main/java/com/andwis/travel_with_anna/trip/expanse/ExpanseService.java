package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.*;
import com.andwis.travel_with_anna.handler.exception.CurrencyNotProvidedException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotFoundException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotSaveException;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpanseService {

    private static final String defaultCurrency = "USD";

    private final ExpanseRepository expanseRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyExchangeClient currencyExchangeService;
    private final TripService tripService;
    private final ItemService itemService;

    public Expanse save(Expanse expanse) {
        return expanseRepository.save(expanse);
    }

    public Expanse findById(Long expanseId) {
        return expanseRepository.findById(expanseId).orElseThrow(() -> new ExpanseNotFoundException(
                "Expanse not found"));
    }

    public CurrencyExchange getCurrencyExchangeByCode(String code) {
        return currencyRepository.findByCode(code).orElseThrow(() -> new CurrencyNotProvidedException(code));
    }

    public ExpanseResponse createOrUpdateExpanse(ExpanseRequest expanseRequest) {
        validateExpanseRequest(expanseRequest);

        if (isValidId(expanseRequest.getExpanseId())) {
            return ExpanseMapper.mapToExpanseResponse(updateExpanse(expanseRequest));
        }

        if (isValidId(expanseRequest.getItemId()) && isValidId(expanseRequest.getTripId())) {
            return ExpanseMapper.mapToExpanseResponse(saveExpanseWithItem(expanseRequest));
        }
        throw new ExpanseNotSaveException("Expanse not saved");
    }

    private boolean isValidId(Long id) {
        return id != null && id != 0;
    }

    private void validateExpanseRequest(ExpanseRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
    }

    private Expanse saveExpanseWithItem(
            @NotNull ExpanseRequest request) {

        Expanse expanse;

        Item item = itemService.findById(request.getItemId());
        if (item.getExpanse() != null) {
            expanse = item.getExpanse();
        } else {
            expanse = new Expanse();
        }
        ExpanseMapper.mapToExpanse(expanse, request);

        item.addExpanse(expanse);

        Trip trip = tripService.getTripById(request.getTripId());
        trip.addExpanse(expanse);

        save(expanse);
        return expanse;
    }

    private Expanse updateExpanse(@NotNull ExpanseRequest request) {
        Expanse expanse = findById(request.getExpanseId());
        ExpanseMapper.mapToExpanse(expanse, request);
        save(expanse);
        return expanse;
    }

    public ExpanseResponse getExpanseById(Long expanseId) {
        Expanse expanse = expanseRepository.findById(expanseId).orElseThrow();
        return ExpanseMapper.mapToExpanseResponse(expanse);
    }

    public List<ExpanseResponse> getExpansesForTrip(Long tripId) {
        List<Expanse> expanses = expanseRepository.findByTripId(tripId);
        return ExpanseMapper.mapToExpanseResponseList(expanses);
    }

    public ExpanseResponse getExpanseByItemId(Long itemId) {
        Item item = itemService.findById(itemId);
        if (item == null) {
            return null;
        }
        Expanse expanse = item.getExpanse();
        if (expanse == null) {
            return null;
        }
        Long expanseId = expanse.getExpanseId();
        return getExpanseById(expanseId);
    }

    public BigDecimal getExchangeRate(String currencyFrom, String currencyTo) {
        verifyCurrencyExchange();
        updateCurrencyExchange();
        BigDecimal exchangeRate = calculateExchangeRate(currencyFrom, currencyTo);
        if (exchangeRate == null) {
            return BigDecimal.ONE;
        }
        return exchangeRate.setScale(5, RoundingMode.HALF_UP);
    }

    private @Nullable BigDecimal calculateExchangeRate(String currencyFrom, String currencyTo) {
        CurrencyExchange currencyFromExchange;
        CurrencyExchange currencyToExchange;
        try {
            currencyFromExchange = getCurrencyExchangeByCode(currencyFrom);
            currencyToExchange = getCurrencyExchangeByCode(currencyTo);
        } catch (CurrencyNotProvidedException e) {
            return null;
        }
        BigDecimal getUSD = BigDecimal.ONE.divide(currencyFromExchange.getExchangeValue(), 12, RoundingMode.HALF_UP);
        return getUSD.multiply(currencyToExchange.getExchangeValue().setScale(12, RoundingMode.HALF_UP));
    }


    private void verifyCurrencyExchange() {
        if (currencyRepository.count() == 0) {
            saveAllCurrencyExchange();
        }
    }

    private void updateCurrencyExchange() {
        if (currencyRepository.count() > 0) {
            CurrencyExchange currencyExchange = getCurrencyExchangeByCode(defaultCurrency);
            if(currencyExchange != null &&
                    currencyExchange.getTimeStamp().isBefore(
                            LocalDateTime.now().minusHours(6))) {
                saveAllCurrencyExchange();
            }
        }
    }

    private void saveAllCurrencyExchange() {
        currencyRepository.deleteAll();
        List<CurrencyExchangeResponse> currencyApiFetch = currencyExchangeService.getAllExchangeRates();
        List<CurrencyExchange> currencyExchangeList = currencyApiFetch.stream()
                .map(CurrencyExchangeMapper::mapToCurrencyExchange)
                .toList();
        currencyRepository.saveAll(currencyExchangeList);
    }

    public ExpanseInTripCurrency getExpanseInTripCurrency(BigDecimal price, BigDecimal paid, BigDecimal exchangeRate) {

        BigDecimal priceInTripCurrency = calculatePriceInTripCurrency(price, exchangeRate);
        priceInTripCurrency = priceInTripCurrency.setScale(2, RoundingMode.HALF_UP);

        BigDecimal paidInTripCurrency = calculatePriceInTripCurrency(paid, exchangeRate);
        paidInTripCurrency = paidInTripCurrency.setScale(2, RoundingMode.HALF_UP);

        return new ExpanseInTripCurrency(priceInTripCurrency, paidInTripCurrency);
    }

    @Contract(pure = true)
    private @NotNull BigDecimal calculatePriceInTripCurrency(@NotNull BigDecimal value, BigDecimal exchangeRate) {
        return value.multiply(exchangeRate);
    }

    public void changeTripCurrency(@NotNull Budget budget) {
        List<Expanse> expanses = expanseRepository.findByTripId(budget.getTrip().getTripId());
        expanses.forEach(expanse -> {
            BigDecimal exchangeRate = getExchangeRate(expanse.getCurrency(), budget.getCurrency());
            expanse.setExchangeRate(exchangeRate);
        });
        expanseRepository.saveAll(expanses);
    }
}
