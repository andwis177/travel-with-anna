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
    private final AdminFacade facade;

    @GetMapping("/users")
    public PageResponse<UserAdminResponse> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            @AuthenticationPrincipal UserDetails connectedUser) {
        return facade.getAllUsers(page, size, connectedUser);
    }

    @GetMapping("/user/{identifier}")
    public ResponseEntity<UserAdminResponse> getUserAdminViewByIdentifier(
            @PathVariable String identifier,
            @AuthenticationPrincipal UserDetails connectedUser) {
        UserAdminResponse user = facade.getUserAdminViewByIdentifier(identifier, connectedUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/avatar/{userId}")
    public ResponseEntity<AvatarImg> getAvatar(@PathVariable Long userId) {
        AvatarImg userAvatar = facade.getAvatar(userId);
        return ResponseEntity.ok(userAvatar);
    }

    @PatchMapping
    public ResponseEntity<Void> updateUser(
            @RequestBody @Valid UserAdminUpdateRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws RoleNotFoundException, WrongPasswordException {
        facade.updateUser(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserResponse> deleteUser(
            @RequestBody @Valid UserAdminDeleteRequest request,
            @AuthenticationPrincipal UserDetails connectedUser) {
        UserResponse respond = facade.deleteUser(request, connectedUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respond);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoleNamesWithAdmin() {
        return ResponseEntity.ok(facade.getAllRoleNamesWithAdmin());
    }
}
