package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
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
            UserCredentialsRequest userCredentials, UserDetails connectedUser)
            throws WrongPasswordException {
        return userService.updateUserExecution(userCredentials, connectedUser);
    }

    public UserResponse changePassword(
            ChangePasswordRequest request, UserDetails connectedUser)
            throws WrongPasswordException {
        return userService.changePassword(request, connectedUser);
    }

    public UserResponse deleteConnectedUser(PasswordRequest request, UserDetails connectedUser)
            throws UsernameNotFoundException, WrongPasswordException {
        return userService.deleteConnectedUser(request, connectedUser);
    }
}
