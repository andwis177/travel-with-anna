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

    private static final String UNAUTHORIZED_ACCESS_MESSAGE =
            "You are not authorized to modify or view this backpack or items";
    private static final String BACKPACK_NOT_FOUND_MESSAGE = "Backpack not found";

    private final BackpackRepository backpackRepository;
    private final UserAuthenticationService userService;

    public void authorizeBackpackAccess(Long backpackId, UserDetails connectedUser) {
        Backpack backpack = fetchBackpackById(backpackId);
        verifyOwner(backpack, connectedUser);
    }

    public void authorizeItemSave(@NotNull List<Long> itemIds, @NotNull UserDetails connectedUser) {
        Set<Backpack> backpacks = backpackRepository.findBackpacksByItemIds(itemIds);
        backpacks.forEach(backpack -> verifyOwner(backpack, connectedUser));
    }

    public void verifyBackpackOwner(@NotNull Backpack backpack, @NotNull UserDetails connectedUser) {
        verifyOwner(backpack, connectedUser);
    }

    private Backpack fetchBackpackById(Long backpackId) {
        return backpackRepository.findById(backpackId).orElseThrow(
                () -> new BackpackNotFoundException(BACKPACK_NOT_FOUND_MESSAGE)
        );
    }

    private void verifyOwner(@NotNull Backpack backpack, @NotNull UserDetails connectedUser) {
        userService.validateOwnership(backpack, connectedUser, UNAUTHORIZED_ACCESS_MESSAGE);
    }
}
