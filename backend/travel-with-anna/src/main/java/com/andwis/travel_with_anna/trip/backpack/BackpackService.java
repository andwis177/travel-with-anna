package com.andwis.travel_with_anna.trip.backpack;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BackpackService {
    private final BackpackRepository backpackRepository;

    public void saveBackpack(Backpack backpack) {
        backpackRepository.save(backpack);
    }
}
