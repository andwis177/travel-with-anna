package com.andwis.travel_with_anna.auth;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.management.relation.RoleNotFoundException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {
    private final AuthenticationService service;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws RoleNotFoundException, MessagingException {
        service.register(request);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    )  {
        AuthenticationResponse response = service.authenticate(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activate-account")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirm(
            @RequestParam String token
    ) throws MessagingException {
        service.activateAccount(token);
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<?> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request
    ) throws MessagingException {
        service.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
