package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.handler.exception.ItemNotFoundException;
import com.andwis.travel_with_anna.trip.backpack.BackpackAuthorizationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
                .quantity(itemRequest.getQty())
                .isPacked(itemRequest.isPacked())
                .build();
    }

    public List<ItemResponse> getAllItemsByBackpackId(Long backpackId, UserDetails connectedUser) {
        backpackAuthorizationService.getAllItemsByBackpackIdAuthorization(backpackId, connectedUser);
        return itemRepository.findAllByBackpackId(backpackId).stream()
                .map(ItemMapper::toItemResponse)
                .toList();
    }

    @Transactional
    public void saveAllItems(@NotNull List<ItemResponse> items, UserDetails connectedUser) {
        List<Long> idList = items.stream()
                .map(ItemResponse::itemId)
                .toList();
        backpackAuthorizationService.saveAllItemAuthorization(idList, connectedUser);
        List<Item> itemsToSave = itemRepository.findAllById(idList);
        itemsToSave
                .forEach(item -> {
                    ItemResponse itemRequest = items.stream()
                            .filter(i -> i.itemId().equals(item.getItemId()))
                            .findFirst()
                            .orElseThrow();
                    item.setPacked(itemRequest.isPacked());
                    item.setQuantity(
                            validateLength(itemRequest.qty(), 40));
                    item.setItemName(
                            validateLength(itemRequest.itemName(), 60));
                });
        itemRepository.saveAll(itemsToSave);
    }

    private @NotNull String validateLength(@NotNull String string, int length) {
        if (string.length() > length) {
            return string.substring(0, length);
        }
        return string;
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
