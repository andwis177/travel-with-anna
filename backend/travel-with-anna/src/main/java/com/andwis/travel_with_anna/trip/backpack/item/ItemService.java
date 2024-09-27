package com.andwis.travel_with_anna.trip.backpack.item;

import com.andwis.travel_with_anna.handler.exception.ItemNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;

    public void saveItem(Item item) {
        itemRepository.save(item);
    }

    public Item findById(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
    }

    public Item createItem(ItemWithExpanseRequest itemWithExpanseRequest) {
        return Item.builder()
                .itemName(itemWithExpanseRequest.getItemName())
                .quantity(itemWithExpanseRequest.getQty())
                .isPacked(itemWithExpanseRequest.isPacked())
                .build();
    }

    public List<ItemResponse> getAllItemsByBackpackId(Long backpackId) {
        return itemRepository.findAllByBackpackId(backpackId).stream()
                .map(ItemMapper::toItemResponse)
                .toList();
    }

    public void saveAllItems(List<ItemRequest> items) {
        List<Long> idList = items.stream()
                .map(ItemRequest::getItemId)
                .toList();
        List<Item> itemsToSave = itemRepository.findAllById(idList);
        itemsToSave
                .forEach(item -> {
                    ItemRequest itemRequest = items.stream()
                            .filter(i -> i.getItemId().equals(item.getItemId()))
                            .findFirst()
                            .orElseThrow();
                    item.setPacked(itemRequest.isPacked());
                    item.setQuantity(
                            validateLength(itemRequest.getQty(), 40));
                    item.setItemName(
                            validateLength(itemRequest.getItemName(), 60));
                });
        itemRepository.saveAll(itemsToSave);
    }

    private String validateLength(String string, int length) {
        if (string.length() > length) {
            return string.substring(0, length);
        }
        return string;
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
