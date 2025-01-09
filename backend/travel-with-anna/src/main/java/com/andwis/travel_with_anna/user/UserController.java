package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.RoleNameResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserFacade facade;

    @GetMapping("/credentials")
    public ResponseEntity<UserCredentialsResponse> getCredentials(
            @AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.getCredentials(connectedUser.getUsername()));
    }

    @GetMapping("/role")
    public ResponseEntity<RoleNameResponse> fetchRole(@AuthenticationPrincipal UserDetails connectedUser) {
        return ResponseEntity.ok(facade.fetchUserRoleName(connectedUser));
    }

    @PatchMapping
    public ResponseEntity<AuthenticationResponse> update(
            @RequestBody @Valid UserCredentialsRequest userCredentials,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        return ResponseEntity.ok(facade.updateUserExecution(userCredentials, connectedUser));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        return ResponseEntity.accepted().body(facade.changePassword(request, connectedUser));
    }

    @DeleteMapping
    public ResponseEntity<UserResponse> delete(
            @RequestBody @Valid PasswordRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(facade.deleteConnectedUser(request, connectedUser));
    }
}
