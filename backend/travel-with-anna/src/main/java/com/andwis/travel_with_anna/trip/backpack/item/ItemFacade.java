package com.andwis.travel_with_anna.trip.backpack.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemFacade {
    private final ItemService itemService;

    public void saveAllItems(List<ItemRequest> items) {
        itemService.saveAllItems(items);
    }

    public List<ItemRequest> getAllItemsByBackpackId(Long backpackId) {
        return itemService.getAllItemsByBackpackId(backpackId);
    }
}
