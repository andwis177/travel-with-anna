package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.andwis.travel_with_anna.trip.expanse.ExpanseMapper.toExpanseResponse;

@Service
@RequiredArgsConstructor
public class ExpanseFacade {
    private final ExpanseService expanseService;
    private final ItemService itemService;
    private final ActivityService activityService;

    public ExpanseResponse createOrUpdateExpanse(@NotNull ExpanseRequest expanseRequest) {
        ExpanseType expanseType = ExpanseType.fromString(expanseRequest.getEntityType());

        if (expanseType == null) {
            return updateExpanse(expanseRequest);
        } else {
            return switch (expanseType) {
                case ITEM -> createOrUpdateExpanseForItem(expanseRequest);
                case ACTIVITY -> createOrUpdateExpanseForActivity(expanseRequest);
            };
        }
    }

    public ExpanseResponse getExpanseByEntityId(Long entityId, @NotNull @NotEmpty @NotBlank String entityType) {
        ExpanseType expanseType = ExpanseType.fromString(entityType);

        if (expanseType == null) {
            return new ExpanseResponse(
                    -1L,
                    "",
                    "",
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    BigDecimal.ONE,
                    BigDecimal.ZERO,
                    BigDecimal.ZERO
            );
        }
        return switch (expanseType) {
            case ITEM -> getExpanseByItemId(entityId);
            case ACTIVITY -> getExpanseByActivityId(entityId);
        };
    }

    private ExpanseResponse createOrUpdateExpanseForItem(
            @NotNull ExpanseRequest expanseRequest) {
        Item item = itemService.findById(expanseRequest.getEntityId());
        return expanseService.createOrUpdateExpanse(
                expanseRequest,
                id -> item,
                item::getExpanse,
                Item::addExpanse);
    }

    private ExpanseResponse createOrUpdateExpanseForActivity(
            @NotNull ExpanseRequest expanseRequest) {
        Activity activity = activityService.getById(expanseRequest.getEntityId());
        return expanseService.createOrUpdateExpanse(
                expanseRequest,
                id -> activity,
                activity::getExpanse,
                Activity::addExpanse);
    }

    public ExpanseResponse updateExpanse(@NotNull ExpanseRequest expanseRequest) {
        return toExpanseResponse(expanseService.updateExpanse(expanseRequest));
    }

    public ExpanseResponse getExpanseById(Long expanseId) {
        return expanseService.getExpanseById(expanseId);
    }

    private ExpanseResponse getExpanseByItemId(Long itemId) {
        Item item = itemService.findById(itemId);
        return expanseService.getExpanseByEntityId(
                itemId,
                id -> item,
                Item::getExpanse
        );
    }

    private ExpanseResponse getExpanseByActivityId(Long activityId) {
        Activity activity = activityService.getById(activityId);
        return expanseService.getExpanseByEntityId(
                activityId,
                id -> activity,
                Activity::getExpanse
        );
    }

    public ExchangeResponse getExchangeRate(String currencyFrom, String currencyTo) {
        return expanseService.getExchangeRate(currencyFrom, currencyTo);
    }
    public ExpanseInTripCurrency getExpanseInTripCurrency(BigDecimal price, BigDecimal paid, BigDecimal exchangeRate) {
        return expanseService.getExpanseInTripCurrency(price, paid, exchangeRate);
    }
}
