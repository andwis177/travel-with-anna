package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.*;
import com.andwis.travel_with_anna.handler.exception.CurrencyNotProvidedException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotFoundException;
import com.andwis.travel_with_anna.handler.exception.ExpanseNotSaveException;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class ExpanseService {

    private static final String defaultCurrency = "USD";

    private final ExpanseRepository expanseRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyExchangeClient currencyExchangeService;
    private final TripService tripService;

    public Expanse save(Expanse expanse) {
        return expanseRepository.save(expanse);
    }

    public Expanse findById(Long expanseId) {
        if (expanseId == null) {
            throw new ExpanseNotFoundException("Expanse not found");
        }
        return expanseRepository.findById(expanseId).orElseThrow(() -> new ExpanseNotFoundException(
                "Expanse not found"));
    }

    public CurrencyExchange getCurrencyExchangeByCode(String code) {
        return currencyRepository.findByCode(code).orElseThrow(() -> new CurrencyNotProvidedException(code));
    }

    @Transactional
    public <T> ExpanseResponse createOrUpdateExpanse(
            @NotNull ExpanseRequest expanseRequest,
            @NotNull Function<Long, T> getByIdFunction,
            @NotNull Supplier<Expanse> getExpanseFunction,
            @NotNull BiConsumer<T, Expanse> addExpanseFunction
    ){
        if (isValidId(expanseRequest.getExpanseId())) {
            return ExpanseMapper.toExpanseResponse(updateExpanse(expanseRequest));
        }
        if (isValidId(expanseRequest.getEntityId()) && isValidId(expanseRequest.getTripId())) {
            return ExpanseMapper.toExpanseResponse(saveExpanse(
                    expanseRequest, getByIdFunction, getExpanseFunction, addExpanseFunction));
        }
        throw new ExpanseNotSaveException("Expanse not saved");
    }

    private boolean isValidId(Long id) {
        return id != null && id > 0;
    }

    @Transactional
    public <T> Expanse saveExpanse(
            @NotNull ExpanseRequest request,
            @NotNull Function<Long, T> getByIdFunction,
            @NotNull Supplier<Expanse> expanseSupplier,
            @NotNull BiConsumer<T, Expanse> addExpanseFunction
    ) {
        Expanse expanse;

        T entity = getByIdFunction.apply(request.getEntityId());
        expanse = expanseSupplier.get();

        if (expanse == null) {
            expanse = Expanse.builder().build();
        }

        ExpanseMapper.mapToExpanse(expanse, request);
        addExpanseFunction.accept(entity, expanse);

        Trip trip = tripService.getTripById(request.getTripId());
        trip.addExpanse(expanse);
        save(expanse);
        return expanse;
    }

    @Transactional
    public Expanse updateExpanse(@NotNull ExpanseRequest request) {
        if (request.getExpanseId() == null) {
            throw new ExpanseNotFoundException("Expanse not found");
        }
        Expanse expanse = findById(request.getExpanseId());
        ExpanseMapper.mapToExpanse(expanse, request);
        save(expanse);
        return expanse;
    }

    public ExpanseResponse getExpanseById(Long expanseId) {
        Expanse expanse = expanseRepository.findById(expanseId).orElseThrow();
        return ExpanseMapper.toExpanseResponse(expanse);
    }

    public List<ExpanseResponse> getExpansesForTrip(Long tripId) {
        List<Expanse> expanses = expanseRepository.findByTripId(tripId);
        return ExpanseMapper.mapToExpanseResponseList(expanses);
    }

    public <T> ExpanseResponse getExpanseByEntityId(
            @NotNull Long entityId,
            @NotNull Function<Long, T> getByIdFunction,
            @NotNull Function<T, Expanse> getExpanseFunction
    ) {
        T entity = getByIdFunction.apply(entityId);
        Expanse expanse = getExpanseFunction.apply(entity);

        if (expanse == null) {
            return null;
        }
        return ExpanseMapper.toExpanseResponse(expanse);
    }

    public ExchangeResponse getExchangeRate(String currencyFrom, String currencyTo) {
        if (currencyFrom == null || currencyFrom.isEmpty() || currencyFrom.isBlank()
                || currencyTo == null || currencyTo.isEmpty() || currencyTo.isBlank()) {
            return new ExchangeResponse("Currency not provided", BigDecimal.ZERO);
        }
        if (currencyFrom.equals(currencyTo)) {
            return new ExchangeResponse(null, BigDecimal.ONE);
        }
        verifyCurrencyExchange();
        updateCurrencyExchange();
        return calculateExchangeRate(currencyFrom, currencyTo);
    }

    private @NotNull ExchangeResponse calculateExchangeRate(String currencyFrom, String currencyTo) {
        CurrencyExchange currencyFromExchange;
        CurrencyExchange currencyToExchange;
        try {
            currencyFromExchange = getCurrencyExchangeByCode(currencyFrom);
            currencyToExchange = getCurrencyExchangeByCode(currencyTo);
        } catch (CurrencyNotProvidedException e) {
            return new ExchangeResponse(("Currency not supported"), BigDecimal.ZERO);
        }
        BigDecimal getUSD = BigDecimal.ONE.divide(currencyFromExchange.getExchangeValue(), 12, RoundingMode.HALF_UP);
        return new ExchangeResponse(null ,
                getUSD.multiply(currencyToExchange.getExchangeValue().setScale(5, RoundingMode.HALF_UP)));
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
        List<CurrencyExchangeResponse> currencyApiFetch = currencyExchangeService.fetchAllExchangeRates();
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

    @Transactional
    public void changeTripCurrency(@NotNull Budget budget) {
        List<Expanse> expanses = expanseRepository.findByTripId(budget.getTrip().getTripId());
        expanses.forEach(expanse -> {
            ExchangeResponse exchangeResponse = getExchangeRate(expanse.getCurrency(), budget.getCurrency());
            expanse.setExchangeRate(exchangeResponse.exchangeRate());
        });
        expanseRepository.saveAll(expanses);
    }
}
