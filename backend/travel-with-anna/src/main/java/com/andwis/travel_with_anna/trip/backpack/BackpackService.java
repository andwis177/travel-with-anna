package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemCreator;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.note.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackpackService {
    private final BackpackRepository backpackRepository;
    private final ItemService itemService;
    private final NoteService noteService;

    public Backpack findById(Long backpackId) {
        return backpackRepository.findById(backpackId).orElseThrow(BackpackNotFoundException::new);
    }

    public void addItemToBackpack(Long backpackId, ItemCreator itemCreator) {
        Backpack backpack = findById(backpackId);
        Item item = itemService.createItem(itemCreator);
        backpack.addItem(item);
        itemService.saveItem(item);
    }

    public BackpackRequest getBackpackById(Long backpackId) {
        Backpack backpack = findById(backpackId);
        boolean isNote = noteService.isNoteExists(backpackId);
        return BackpackMapper.toBackpackRequest(backpack, isNote);
    }

    public void deleteItem(Long itemId) {
        itemService.deleteItem(itemId);
    }
}
