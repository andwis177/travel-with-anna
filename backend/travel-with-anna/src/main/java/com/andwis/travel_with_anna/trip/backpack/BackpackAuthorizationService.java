package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.user.UserAuthenticationService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class BackpackAuthorizationService {
    private final static String BACKPACK_MSG_EXCEPTION =
            "You are not authorized to modify or view this backpack or items";

    private final BackpackRepository backpackRepository;
    private final UserAuthenticationService userService;

    public void getAllItemsByBackpackIdAuthorization(Long backpackId, UserDetails connectedUser) {
        Backpack backpack = backpackRepository.findById(backpackId).orElseThrow(
                () -> new BackpackNotFoundException("Backpack not found"));
        userService.verifyOwner(backpack, connectedUser, BACKPACK_MSG_EXCEPTION);
    }

    public void saveAllItemAuthorization(@NotNull List<Long> itemIds, UserDetails connectedUser) {
        Set<Backpack> backpacks = backpackRepository.findBackpacksByItemIds(itemIds);
        backpacks.forEach(backpack -> userService.verifyOwner(backpack, connectedUser, BACKPACK_MSG_EXCEPTION));
    }

    public void verifyBackpackOwner(@NotNull Backpack backpack, UserDetails connectedUser) {
        userService.verifyOwner(backpack, connectedUser, BACKPACK_MSG_EXCEPTION);
    }
}
