package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {
    private final UserService userService;

    public UserCredentialsResponse getCredentials(String email) {
        return userService.getCredentials(email);
    }

    public AuthenticationResponse updateUserExecution(
            UserCredentialsRequest userCredentials, Authentication connectedUser)
            throws WrongPasswordException {
        return userService.updateUserExecution(userCredentials, connectedUser);
    }

    public UserResponse changePassword(
            ChangePasswordRequest request, Authentication connectedUser)
            throws WrongPasswordException {
        return userService.changePassword(request, connectedUser);
    }

    public UserResponse deleteConnectedUser(PasswordRequest request, Authentication connectedUser)
            throws UsernameNotFoundException, WrongPasswordException {
        return userService.deleteConnectedUser(request, connectedUser);
    }
}
