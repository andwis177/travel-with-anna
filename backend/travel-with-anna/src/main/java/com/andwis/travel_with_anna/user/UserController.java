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

    private final UserFacade facade;

    @GetMapping("/credentials")
    public ResponseEntity<UserCredentials> getCredentials(Authentication connectedUser) {
        UserCredentials userCredentials = facade.getCredentials(connectedUser.getName());
        return ResponseEntity.ok(userCredentials);
    }

    @PatchMapping("/update")
    public ResponseEntity<AuthenticationResponse> update(@RequestBody @Valid UserCredentials userCredentials, Authentication connectedUser) {
        AuthenticationResponse response = facade.updateUserExecution(userCredentials, connectedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    public ResponseEntity<UserRespond> changePassword(@RequestBody @Valid ChangePasswordRequest request, Authentication connectedUser) {
        UserRespond respond = facade.changePassword(request, connectedUser);
        return ResponseEntity.accepted().body(respond);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserRespond> delete(@RequestBody @Valid PasswordRequest request, Authentication connectedUser) {
        UserRespond respond = facade.deleteConnectedUser(request, connectedUser);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respond);
    }
}
