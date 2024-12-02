package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.trip.backpack.item.ItemRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackpackFacade {
    private final BackpackService backpackService;

    public void addItemToBackpack(
            Long backpackId,
            ItemRequest itemRequest,
            UserDetails connectedUser) {
        backpackService.addItemToBackpack(backpackId, itemRequest, connectedUser);
    }

    public BackpackResponse getBackpackById(Long backpackId, UserDetails connectedUser) {
        return backpackService.getBackpackById(backpackId, connectedUser);
    }

    public void deleteItem(Long itemId, UserDetails connectedUser) {
        backpackService.deleteItem(itemId, connectedUser);
    }
}
