package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemService;
import com.andwis.travel_with_anna.trip.backpack.item.ItemWithExpanseRequest;
import com.andwis.travel_with_anna.trip.note.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BackpackService {
    private final BackpackRepository backpackRepository;
    private final ItemService itemService;
    private final NoteService noteService;

    public Backpack findById(Long backpackId) {
        return backpackRepository.findById(backpackId).orElseThrow(BackpackNotFoundException::new);
    }

    @Transactional
    public void addItemToBackpack(Long backpackId, ItemWithExpanseRequest itemWithExpanseRequest) {
        Backpack backpack = findById(backpackId);
        Item item = itemService.createItem(itemWithExpanseRequest);
        backpack.addItem(item);
        itemService.saveItem(item);
    }

    public BackpackResponse getBackpackById(Long backpackId) {
        Backpack backpack = findById(backpackId);
        boolean isNote = noteService.isNoteExists(backpackId);
        return BackpackMapper.toBackpackResponse(backpack, isNote);
    }

    @Transactional
    public void deleteItem(Long itemId) {
        Item item = itemService.findById(itemId);

        Backpack backpack = item.getBackpack();
        if (backpack != null) {
            backpack.removeItem(item);
        }
        itemService.deleteItem(itemId);
    }
}
