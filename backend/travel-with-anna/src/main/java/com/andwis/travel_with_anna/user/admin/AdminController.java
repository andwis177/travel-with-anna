package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.user.UserRespond;
import com.andwis.travel_with_anna.utility.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
    public PageResponse<UserAdminView> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return facade.getAllUsers(page, size, connectedUser);
    }

    @GetMapping("/user/{identifier}")
    public ResponseEntity<UserAdminView> getUserAdminViewByIdentifier(@PathVariable String identifier, Authentication connectedUser) {
        UserAdminView user = facade.getUserAdminViewByIdentifier(identifier, connectedUser);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/avatar/{userId}")
    public ResponseEntity<UserAvatar> getAvatar(@PathVariable Long userId) {
        UserAvatar userAvatar = facade.getAvatar(userId);
        return ResponseEntity.ok(userAvatar);
    }

    @PatchMapping("/update")
    public ResponseEntity<Void> updateUser(
            @RequestBody @Valid UserAdminUpdateRequest request,
            Authentication authentication)
            throws RoleNotFoundException {
        facade.updateUser(request, authentication);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserRespond> deleteUser(
            @RequestBody @Valid UserAdminDeleteRequest request,
            Authentication authentication) {
        UserRespond respond = facade.deleteUser(request, authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respond);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoleNamesWithAdmin() {
        return ResponseEntity.ok(facade.getAllRoleNamesWithAdmin());
    }
}
