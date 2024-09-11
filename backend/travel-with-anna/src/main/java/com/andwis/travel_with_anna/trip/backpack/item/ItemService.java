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

    public Item createItem(ItemCreator itemCreator) {
        return Item.builder()
                .item(itemCreator.getItem())
                .quantity(itemCreator.getQty())
                .isPacked(itemCreator.isPacked())
                .build();
    }

    public List<ItemRequest> getAllItemsByBackpackId(Long backpackId) {
        return itemRepository.findAllByBackpackId(backpackId).stream()
                .map(ItemMapper::toItemRequest)
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
                    item.setQuantity(itemRequest.getQty());
                    item.setItem(itemRequest.getItem());
                });
        itemRepository.saveAll(itemsToSave);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);
    }
}
