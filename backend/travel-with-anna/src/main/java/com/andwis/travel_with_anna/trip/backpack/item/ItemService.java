package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.handler.exception.ItemNotFoundException;
import com.andwis.travel_with_anna.trip.backpack.BackpackAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final BackpackAuthorizationService backpackAuthorizationService;

    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
    }

    public Item createItem(@NotNull ItemRequest itemRequest) {
        return Item.builder()
                .itemName(itemRequest.getItemName())
                .quantity(itemRequest.getQuantity())
                .isPacked(itemRequest.isPacked())
                .build();
    }

    public List<ItemResponse> getAllItemsByBackpackId(Long backpackId, UserDetails connectedUser) {
        backpackAuthorizationService.authorizeBackpackAccess(backpackId, connectedUser);
        return itemRepository.findAllByBackpackId(backpackId).stream()
                .map(ItemMapper::toItemResponse)
                .toList();
    }

    @Transactional
    public void saveAllItems(@NotNull List<ItemResponse> items, UserDetails connectedUser) {
        List<Long> idList = items.stream()
                .map(ItemResponse::itemId)
                .toList();

        backpackAuthorizationService.authorizeItemSave(idList, connectedUser);

        List<Item> itemsToSave = itemRepository.findAllById(idList);

        var responseMap = items.stream()
                .collect(Collectors.toMap(ItemResponse::itemId, response -> response));

        itemsToSave.forEach(item -> updateItemFromResponse(item, responseMap.get(item.getItemId())));
        itemRepository.saveAll(itemsToSave);
    }

    private void updateItemFromResponse(Item item, ItemResponse itemResponse) {
        if (itemResponse == null) return;
        item.setPacked(itemResponse.isPacked());
        item.setQuantity(truncateStringToMaxLength(itemResponse.qty(), Item.QUANTITY_MAX_LENGTH));
        item.setItemName(truncateStringToMaxLength(itemResponse.itemName(), Item.ITEM_NAME_MAX_LENGTH));
    }

    private @NotNull String truncateStringToMaxLength(@NotNull String input, int maxLength) {
        return input.length() > maxLength ? input.substring(0, maxLength) : input;
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
