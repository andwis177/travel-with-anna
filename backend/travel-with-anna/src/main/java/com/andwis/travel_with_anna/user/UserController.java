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

    @GetMapping("/profile")
    public ResponseEntity<UserCredentials> getProfile(Authentication connectedUser) {
        UserCredentials userCredentials = service.getProfile(connectedUser);
        return ResponseEntity.ok(userCredentials);
    }

    @PatchMapping("/update")
    public ResponseEntity<AuthenticationResponse> update(@Valid @RequestBody UserCredentials userCredentials, Authentication connectedUser) {
        AuthenticationResponse response = service.updateUserExecution(userCredentials, connectedUser);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/change-password")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<UserRespond> changePassword(@Valid @RequestBody ChangePasswordRequest request, Authentication connectedUser) {
        UserRespond respond = service.changePassword(request, connectedUser);
        return ResponseEntity.ok(respond);
    }

    @DeleteMapping("/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<UserRespond> delete(@Valid @RequestBody PasswordRequest request, Authentication connectedUser) {
       UserRespond respond =service.deleteUser(request, connectedUser);
       return ResponseEntity.ok(respond);
    }
}
