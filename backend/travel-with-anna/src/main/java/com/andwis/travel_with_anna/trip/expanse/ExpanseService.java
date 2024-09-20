package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.*;
import com.andwis.travel_with_anna.handler.exception.CurrencyNotProvidedException;
import com.andwis.travel_with_anna.handler.exception.ItemNotFoundException;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripService;
import lombok.RequiredArgsConstructor;
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

    public void saveExpanse(Expanse expanse) {
        expanseRepository.save(expanse);
    }

    public CurrencyExchange getCurrencyExchangeByCode(String code) {
        return currencyRepository.findByCode(code).orElseThrow(() -> new CurrencyNotProvidedException(code));
    }

    public ExpanseResponse createOrUpdateExpanse(ExpanseForItemRequest expanseCreator) {
        Item item = itemService.findById(expanseCreator.itemId());
        Expanse expanse;
        if (item.getExpanse() != null) {
            expanse = updateItemExpanse(expanseCreator, item);
        } else {
            expanse = createNewItemExpanse(expanseCreator, item);
        }
        return ExpanseMapper.mapToExpanseItem(expanse);
    }

    private Expanse createNewItemExpanse(
            ExpanseForItemRequest request,
            Item item
    ) {
        Expanse expanse = ExpanseMapper.mapToExpanse(
                request.expanseItem());

        Trip trip = tripService.getTripById(
                request.tripId());
        trip.addExpanse(expanse);
        item.addExpanse(expanse);

        saveExpanse(expanse);
        return expanse;
    }

    private Expanse updateItemExpanse(
            ExpanseForItemRequest expanseCreator, Item item) {
        Expanse expanse = item.getExpanse();
        ExpanseRequest expanseItem = expanseCreator.expanseItem();
        expanse.setExpanseName(expanseItem.getExpanseName());
        expanse.setCurrency(expanseItem.getCurrency());
        expanse.setPrice(expanseItem.getPrice());
        expanse.setPaid(expanseItem.getPaid());
        expanse.setExchangeRate(expanseItem.getExchangeRate());
        saveExpanse(expanse);
        return expanse;
    }

    public ExpanseResponse getExpanseById(Long expanseId) {
        Expanse expanse = expanseRepository.findById(expanseId).orElseThrow();
        return ExpanseMapper.mapToExpanseItem(expanse);
    }

    public List<ExpanseResponse> getExpansesForTrip(Long tripId) {
        List<Expanse> expanses = expanseRepository.findByTripId(tripId);
        return ExpanseMapper.mapToExpanseItemList(expanses);
    }

    public ExpanseResponse getExpanseForItem(Long itemId) {
        if (itemId == null || itemId == 0) {
            return null;
        }
        try{
            Item item = itemService.findById(itemId);
            Long expanseId = item.getExpanse().getExpanseId();

            return getExpanseById(expanseId);
        } catch (NullPointerException | ItemNotFoundException e) {
            return null;
        }
    }

    public BigDecimal getExchangeRate(String currencyFrom, String currencyTo) {
        verifyCurrencyExchange();
        updateCurrencyExchange();
        BigDecimal exchangeRate = calculateExchangeRate(currencyFrom, currencyTo);
        if (exchangeRate == null) {
            return BigDecimal.ZERO;
        }
        return exchangeRate.setScale(5, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateExchangeRate(String currencyFrom, String currencyTo) {
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

    private BigDecimal calculatePriceInTripCurrency(BigDecimal value, BigDecimal exchangeRate) {
        return value.multiply(exchangeRate);
    }
}
