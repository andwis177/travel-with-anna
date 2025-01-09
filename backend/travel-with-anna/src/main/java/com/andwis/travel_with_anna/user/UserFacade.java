package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.RoleNameResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFacade {

    private final UserService userService;
    private final UserAuthenticationService userAuthenticationService;

    public UserCredentialsResponse getCredentials(String email) {
        return userService.getCredentials(email);
    }

    public RoleNameResponse fetchUserRoleName(UserDetails connectedUser) {
        return userAuthenticationService.getUserRoleName(connectedUser);
    }

    public AuthenticationResponse updateUserExecution(
            UserCredentialsRequest userCredentials, UserDetails connectedUser)
            throws WrongPasswordException {
        return userService.updateUserDetails(userCredentials, connectedUser);
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
