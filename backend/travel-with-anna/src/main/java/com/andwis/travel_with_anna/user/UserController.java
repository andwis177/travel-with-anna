package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
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
        UserCredentialsResponse userCredentials = facade.getCredentials(connectedUser.getUsername());
        return ResponseEntity.ok(userCredentials);
    }

    @PatchMapping
    public ResponseEntity<AuthenticationResponse> update(
            @RequestBody @Valid UserCredentialsRequest userCredentials,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        AuthenticationResponse response = facade.updateUserExecution(userCredentials, connectedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<UserResponse> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        UserResponse respond = facade.changePassword(request, connectedUser);
        return ResponseEntity.accepted().body(respond);
    }

    @DeleteMapping
    public ResponseEntity<UserResponse> delete(
            @RequestBody @Valid PasswordRequest request,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws WrongPasswordException {
        UserResponse respond = facade.deleteConnectedUser(request, connectedUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respond);
    }
}
