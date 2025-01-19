package com.andwis.travel_with_anna.trip.expanse;

import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import static com.andwis.travel_with_anna.trip.expanse.ExpanseMapper.toExpanseResponse;

@Service
@RequiredArgsConstructor
public class ExpanseFacade {

    private static final ExpanseResponse DEFAULT_EXPANSE_RESPONSE = new ExpanseResponse(
            -1L, "", "", "", "",
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO
    );

    private final ExpanseService expanseService;
    private final ItemService itemService;
    private final ActivityService activityService;

    public ExpanseResponse createOrUpdateExpanse(
            @NotNull @NotEmpty @NotBlank ExpanseRequest expanseRequest, UserDetails connectedUser) {
        ExpanseType expanseType = ExpanseType.fromString(expanseRequest.getEntityType());
        if (expanseType == null) {
            return updateExpanse(expanseRequest, connectedUser);
        }
        return
                createOrUpdateForSpecificEntity(expanseRequest, expanseType, connectedUser);
    }

    private ExpanseResponse createOrUpdateForSpecificEntity(
            @NotNull ExpanseRequest expanseRequest, @NotNull @NotEmpty @NotBlank ExpanseType expanseType, UserDetails connectedUser) {
        return switch (expanseType) {
            case ITEM -> createOrUpdateExpanseForItem(expanseRequest, connectedUser);
            case ACTIVITY -> createOrUpdateExpanseForActivity(expanseRequest, connectedUser);
        };
    }

    private ExpanseResponse createOrUpdateExpanseForItem(
            @NotNull ExpanseRequest expanseRequest,
            UserDetails connectedUser) {
        Item item = itemService.findById(expanseRequest.getEntityId());
        return expanseService.createOrUpdateExpanse(
                expanseRequest,
                itemId -> item,
                item::getExpanse,
                Item::addExpanse,
                connectedUser);
    }

    private ExpanseResponse createOrUpdateExpanseForActivity(
            @NotNull ExpanseRequest expanseRequest,
            UserDetails connectedUser) {
        Activity activity = activityService.getById(expanseRequest.getEntityId());
        return expanseService.createOrUpdateExpanse(
                expanseRequest,
                itemId -> activity,
                activity::getExpanse,
                Activity::addExpanse,
                connectedUser);
    }

    private @NotNull ExpanseResponse updateExpanse(@NotNull ExpanseRequest expanseRequest, UserDetails connectedUser) {
        return toExpanseResponse(expanseService.updateExpanse(expanseRequest, connectedUser));
    }

    public ExpanseResponse getExpanseByEntityId(Long entityId, @NotNull @NotEmpty @NotBlank String entityType,
                                                UserDetails connectedUser) {
        ExpanseType expanseType = ExpanseType.fromString(entityType);

        if (expanseType == null) {
            return DEFAULT_EXPANSE_RESPONSE;
        }
        return fetchExpanseForSpecificEntity(entityId, expanseType, connectedUser);
    }

    private ExpanseResponse fetchExpanseForSpecificEntity(
            Long entityId, @NotNull @NotEmpty @NotBlank ExpanseType expanseType,
            @NotNull @NotEmpty @NotBlank UserDetails connectedUser) {
        return switch (expanseType) {
            case ITEM -> getExpanseByItemId(entityId, connectedUser);
            case ACTIVITY -> getExpanseByActivityId(entityId, connectedUser);
        };
    }

    public ExpanseResponse getExpanseById(Long expanseId, UserDetails connectedUser) {
        return expanseService.getExpanseById(expanseId, connectedUser);
    }

    private ExpanseResponse getExpanseByItemId(Long itemId, UserDetails connectedUser) {
        Item item = itemService.findById(itemId);
        return expanseService.getExpanseByEntityId(
                itemId,
                id -> item,
                Item::getExpanse,
                connectedUser
        );
    }

    private ExpanseResponse getExpanseByActivityId(Long activityId, UserDetails connectedUser) {
        Activity activity = activityService.getById(activityId);
        return expanseService.getExpanseByEntityId(
                activityId,
                itemId -> activity,
                Activity::getExpanse,
                connectedUser
        );
    }

    public ExchangeResponse getExchangeRate(String currencyFrom, String currencyTo) {
        return expanseService.getExchangeRate(currencyFrom, currencyTo);
    }
    public ExpanseInTripCurrency getExpanseInTripCurrency(TripCurrencyValuesRequest request) {
        return expanseService.getExpanseInTripCurrency(request);
    }
}
