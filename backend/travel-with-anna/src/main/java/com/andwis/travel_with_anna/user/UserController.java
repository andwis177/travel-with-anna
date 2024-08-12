package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
@RequiredArgsConstructor
@Tag(name = "User")
public class UserController {

    private final UserService service;

    @GetMapping("/credentials")
    public ResponseEntity<UserCredentials> getCredentials(Authentication connectedUser) {
        UserCredentials userCredentials = service.getCredentials(connectedUser.getName());
        return ResponseEntity.ok(userCredentials);
    }

    @PatchMapping("/update")
    public ResponseEntity<AuthenticationResponse> update(@Valid @RequestBody UserCredentials userCredentials, Authentication connectedUser) {
        AuthenticationResponse response = service.updateUserExecution(userCredentials, connectedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<UserRespond> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication connectedUser) {
        UserRespond respond = service.changePassword(request, connectedUser);
        return ResponseEntity.accepted().body(respond);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserRespond> delete(@Valid @RequestBody PasswordRequest request, Authentication connectedUser) {
        UserRespond respond =service.deleteConnectedUser(request, connectedUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respond);
    }
}
