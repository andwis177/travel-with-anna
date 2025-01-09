package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.user.UserResponse;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.utility.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
@Tag(name = "Admin")
public class AdminController {

    private static final String DEFAULT_PAGE = "0";
    private static final String DEFAULT_SIZE = "10";

    private final AdminFacade facade;

    @GetMapping("/users")
    public PageResponse<UserAdminResponse> getAllUsers(
            @RequestParam(name = "page", defaultValue = DEFAULT_PAGE, required = false) int page,
            @RequestParam(name = "size", defaultValue = DEFAULT_SIZE, required = false) int size,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return facade.getAllUsers(page, size, connectedUser);
    }

    @GetMapping("/user/{identifier}")
    public ResponseEntity<UserAdminResponse> getUserByIdentifier(
            @PathVariable String identifier,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.getUserAdminDetails(identifier, connectedUser));
    }

    @GetMapping("/avatar/{userId}")
    public ResponseEntity<AvatarImg> getAvatar(@PathVariable Long userId) {
        return ResponseEntity.ok(facade.getAvatar(userId));
    }

    @PatchMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void updateUser(
            @RequestBody @Valid UserAdminUpdateRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws RoleNotFoundException, WrongPasswordException {
        facade.updateUser(request, connectedUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUser(
            @RequestBody @Valid UserAdminDeleteRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(
                facade.deleteUser(request, connectedUser));
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoleNamesWithAdmin() {
        return ResponseEntity.ok(facade.getAllRoleNamesWithAdmin());
    }
}
