package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.api.currency.*;
import com.andwis.travel_with_anna.handler.exception.CurrencyNotProvidedException;
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
    private final CurrencyExchangeService currencyExchangeService;
    private final TripService tripService;
    private final ItemService itemService;

    public void saveExpanse(Expanse expanse) {
        expanseRepository.save(expanse);
    }

    public CurrencyExchange getCurrencyExchangeByCode(String code) {
        return currencyRepository.findByCode(code).orElseThrow(() -> new CurrencyNotProvidedException(code));
    }

    public ExpanseItem createOrUpdateExpanse(ExpanseItemCreator expanseCreator) {
        Item item = itemService.findById(expanseCreator.itemId());
        Expanse expanse;
        if (item.getExpanse() != null) {
            expanse = updateItemExpanse(expanseCreator, item);
        } else {
            expanse = createNewItemExpanse(expanseCreator, item);
        }
        ExpanseItem respond = ExpanseMapper.mapToExpanseItem(expanse);
        respond.setPriceInTripCurrency(expanse.getPriceInTripCurrency());
        respond.setPaidInTripCurrency(expanse.getPaidInTripCurrency());
        return respond;
    }

    private Expanse createNewItemExpanse(
            ExpanseItemCreator expanseCreator, Item item) {
        Expanse expanse = ExpanseMapper.mapToExpanse(
                expanseCreator.expanseItem());

        Trip trip = tripService.getTripById(
                expanseCreator.tripId());
        trip.addExpanse(expanse);
        expanse.setItem(item);
        item.setExpanse(expanse);

        saveExpanse(expanse);
        return expanse;
    }

    private Expanse updateItemExpanse(
            ExpanseItemCreator expanseCreator, Item item) {
        Expanse expanse = item.getExpanse();
        ExpanseItem expanseItem = expanseCreator.expanseItem();
        expanse.setExpanseName(expanseItem.getExpanseName());
        expanse.setCurrency(expanseItem.getCurrency());
        expanse.setPrice(expanseItem.getPrice());
        expanse.setPaid(expanseItem.getPaid());
        expanse.setExchangeRate(expanseItem.getExchangeRate());
        saveExpanse(expanse);
        return expanse;
    }

    public ExpanseItem getExpanseById(Long expanseId) {
        Expanse expanse = expanseRepository.findById(expanseId).orElseThrow();
        ExpanseItem expanseItem = ExpanseMapper.mapToExpanseItem(expanse);
        expanseItem.setPriceInTripCurrency(expanse.getPriceInTripCurrency());
        expanseItem.setPaidInTripCurrency(expanse.getPaidInTripCurrency());
        return expanseItem;
    }

    public ExpanseItem getExpanseForItem(Long itemId) {
        if (itemId == null || itemId == 0) {
            return null;
        }
        Item item = itemService.findById(itemId);
        try{
            Long expanseId = item.getExpanse().getExpanseId();

            return getExpanseById(expanseId);
        } catch (NullPointerException e) {
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
        CurrencyExchange currencyFromExchange = getCurrencyExchangeByCode(currencyFrom);
        CurrencyExchange currencyToExchange = getCurrencyExchangeByCode(currencyTo);
        if (currencyFromExchange == null || currencyToExchange == null) {
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
        List<CurrencyExchangeDto> currencyApiFetch = currencyExchangeService.getAllExchangeRates();
        List<CurrencyExchange> currencyExchangeList = currencyApiFetch.stream()
                .map(CurrencyExchangeMapper::mapToCurrencyExchange)
                .toList();
        currencyRepository.saveAll(currencyExchangeList);
    }

    public TripCurrencyValue getTripCurrencyValues(BigDecimal price, BigDecimal paid, BigDecimal exchangeRate) {

        BigDecimal priceInTripCurrency = calculatePriceInTripCurrency(price, exchangeRate);
        priceInTripCurrency = priceInTripCurrency.setScale(2, RoundingMode.HALF_UP);

        BigDecimal paidInTripCurrency = calculatePriceInTripCurrency(paid, exchangeRate);
        paidInTripCurrency = paidInTripCurrency.setScale(2, RoundingMode.HALF_UP);

        return new TripCurrencyValue(priceInTripCurrency, paidInTripCurrency);
    }

    private BigDecimal calculatePriceInTripCurrency(BigDecimal value, BigDecimal exchangeRate) {
        return value.multiply(exchangeRate);
    }

}
