package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRespond;
import com.andwis.travel_with_anna.utility.PageResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    private final AdminService adminService;

    @GetMapping("/users")
    public PageResponse<UserAdminView> getAllUsers(
            @RequestParam(name = "page", defaultValue = "0", required = false) int page,
            @RequestParam(name = "size", defaultValue = "10", required = false) int size,
            Authentication connectedUser) {
        return adminService.getAllUsers(page, size, connectedUser);
    }

    @GetMapping("/user/{identifier}")
    public ResponseEntity<UserAdminView> getUserAdminViewByIdentifier(@PathVariable String identifier, Authentication connectedUser) {
        UserAdminView user = adminService.getUserAdminViewByIdentifier(identifier, connectedUser);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/update")
    public ResponseEntity<User> updateUser(@RequestBody UserAdminUpdateRequest request, Authentication authentication) throws RoleNotFoundException {
        User user = adminService.updateUser(request, authentication);
        return ResponseEntity.ok().body(user);
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UserRespond> deleteUser(@RequestBody UserAdminDeleteRequest request, Authentication authentication) {
       UserRespond respond =  adminService.deleteUser(request, authentication);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respond);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<String>> getAllRoleNamesWithAdmin() {
        return ResponseEntity.ok(adminService.getAllRoleNamesWithAdmin());
    }
}
