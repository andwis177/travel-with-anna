package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.ItemWithExpanseRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackpackFacade {
    private final BackpackService backpackService;


    public void addItemToBackpack(Long backpackId, ItemWithExpanseRequest itemWithExpanseRequest) {
        backpackService.addItemToBackpack(backpackId, itemWithExpanseRequest);
    }

    public BackpackResponse getBackpackById(Long backpackId) {
        return backpackService.getBackpackById(backpackId);
    }

    public void deleteItem(Long itemId) {
        backpackService.deleteItem(itemId);
    }
}
