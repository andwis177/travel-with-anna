package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;


    public UserCredentials getProfile(Authentication connectedUser) {
        var user = getSecurityUser(connectedUser);
        return UserCredentials.builder()
                .email(user.getUser().getEmail())
                .userName(user.getUser().getUserName())
                .build();
    }

    private void updateUser(UserCredentials userCredentials, SecurityUser user) {
        var currentUser = user.getUser();
        boolean isChanged = false;

        String newUserName = userCredentials.getUserName();
        String newEmail = userCredentials.getEmail();

        if (newUserName != null
                && !newUserName.isBlank()
                && !newUserName.equals(currentUser.getUserName())) {
            if (userRepository.existsByUserName(newUserName)) {
                throw new UserExistsException("User with this name already exists");
            } else {
                currentUser.setUserName(newUserName);
                isChanged = true;
            }
        }
        if (newEmail != null
                && !newEmail.isBlank()
                && !newEmail.equals(currentUser.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new UserExistsException("Email already exists");
            } else {
                currentUser.setEmail(newEmail);
                isChanged = true;
            }
        }
        if (isChanged) {
            userRepository.save(currentUser);
        }
    }

    private void updateSecurityContext(String userName) {
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User is logged out");
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public AuthenticationResponse updateUserExecution(UserCredentials userCredentials, Authentication connectedUser) {
        var user = getSecurityUser(connectedUser);
        verifyPassword(user.getUser(), userCredentials.getPassword());
        updateUser(userCredentials, user);
        updateSecurityContext(userCredentials.getEmail());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String newJwtToken = jwtService.generateJwtToken(authentication);
        return AuthenticationResponse.builder()
                .token(newJwtToken)
                .build();
    }

    public UserRespond changePassword(ChangePasswordRequest request, Authentication connectedUser) {
        var user = getSecurityUser(connectedUser);
        var currentUser = user.getUser();
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new WrongPasswordException("Passwords do not match");
        }

        verifyPassword(currentUser, request.getCurrentPassword());
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        return UserRespond.builder()
                .message("Password has been changed")
                .build();
    }

    public UserRespond deleteUser(PasswordRequest request, Authentication connectedUser) throws UsernameNotFoundException, WrongPasswordException {
        var securityUser = getSecurityUser(connectedUser);
        var currentUser = securityUser.getUser();
        String userName = currentUser.getUserName();
        verifyPassword(currentUser, request.password());
        userRepository.delete(securityUser.getUser());
        return UserRespond.builder()
                .message("User " + userName + " has been deleted!")
                .build();
    }

    private void verifyPassword(User user, String password) throws WrongPasswordException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        } catch (WrongPasswordException exp) {
            throw new WrongPasswordException("Wrong password");
        }
    }

    private static SecurityUser getSecurityUser(Authentication connectedUser) throws UsernameNotFoundException {
        var securityUser  = (SecurityUser) connectedUser.getPrincipal();
        if (securityUser  == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return securityUser ;
    }
}