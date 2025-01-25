package com.andwis.travel_with_anna.auth;

import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountLockedException;
import java.io.IOException;
import java.security.GeneralSecurityException;

@RestController
@RequestMapping("auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthenticationController {

    private final AuthenticationService service;

    private static final ResponseEntity<Void> ACCEPTED_RESPONSE = ResponseEntity.accepted().build();
    private static final ResponseEntity<Void> OK_RESPONSE = ResponseEntity.ok().build();

    private <T> ResponseEntity<T> buildOkResponse(T body) {
        return ResponseEntity.ok(body);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(
            @RequestBody @Valid RegistrationRequest request
    ) throws Exception {
        service.register(request);
        return ACCEPTED_RESPONSE;
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(
            @RequestBody @Valid AuthenticationRequest request
    ) throws WrongPasswordException, AccountLockedException {
        return buildOkResponse(service.authenticationWithCredentials(request));
    }

    @GetMapping("/activate-account")
    public ResponseEntity<Void> confirm(
            @RequestParam String token
    ) throws Exception {
        service.activateAccount(token);
        return ACCEPTED_RESPONSE;
    }

    @PatchMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @RequestBody @Valid ResetPasswordRequest request
    ) throws MessagingException, GeneralSecurityException, IOException, InterruptedException {
        service.resetPassword(request);
        return OK_RESPONSE;
    }

    @PostMapping("/resend-activation-code")
    public ResponseEntity<Void> resendActivationCode(@RequestParam String email) throws Exception {
        service.sendActivationCodeByRequest(email);
        return OK_RESPONSE;
    }
}
