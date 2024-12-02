package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemRequest;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.note.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BackpackService {
    private final BackpackRepository backpackRepository;
    private final ItemService itemService;
    private final NoteService noteService;
    private final BackpackAuthorizationService backpackAuthorizationService;

    public Backpack findById(Long backpackId) {
        return backpackRepository.findById(backpackId).orElseThrow(() -> new BackpackNotFoundException("Backpack not found"));
    }

    @Transactional
    public void addItemToBackpack(Long backpackId, ItemRequest itemRequest, UserDetails connectedUser) {
        Backpack backpack = findById(backpackId);
        backpackAuthorizationService.verifyBackpackOwner(backpack, connectedUser);
        Item item = itemService.createItem(itemRequest);
        backpack.addItem(item);
        itemService.saveItem(item);
    }

    public BackpackResponse getBackpackById(Long backpackId, UserDetails connectedUser) {
        Backpack backpack = findById(backpackId);
        backpackAuthorizationService.verifyBackpackOwner(backpack, connectedUser);
        boolean isNote = noteService.isNoteExists(backpackId);
        return BackpackMapper.toBackpackResponse(backpack, isNote);
    }

    @Transactional
    public void deleteItem(Long itemId, UserDetails connectedUser) {
        Item item = itemService.findById(itemId);
        Backpack backpack = item.getBackpack();
        backpackAuthorizationService.verifyBackpackOwner(backpack, connectedUser);
        backpack.removeItem(item);
        itemService.deleteItem(itemId);
    }
}
