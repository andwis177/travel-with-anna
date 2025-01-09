package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.TokenExistsException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.RoleNameResponse;
import com.andwis.travel_with_anna.security.OwnByUser;
import com.andwis.travel_with_anna.user.token.TokenRepository;
import com.andwis.travel_with_anna.user.token.TokenService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserAuthenticationService {

    private static final String USER_LOGGED_OUT_ERROR = "User is logged out";
    private static final String WRONG_PASSWORD_ERROR = "Wrong password";

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public User retriveConnectedUser(UserDetails connectedUser) {
        return convertToSecurityUser(connectedUser).getUser();
    }

    public RoleNameResponse getUserRoleName(UserDetails connectedUser) {
        User securityUser = retriveConnectedUser(connectedUser);
        String roleName = securityUser.getRole().getRoleName();
        return new RoleNameResponse(roleName);
    }

    protected void updateSecurityContext(String userName) {
        SecurityUser securityUser = (SecurityUser) userDetailsService.loadUserByUsername(userName);
        if (securityUser == null) {
            throw new UsernameNotFoundException(USER_LOGGED_OUT_ERROR);
        }
        setAuthenticationInContext(securityUser);
    }

    private void setAuthenticationInContext(SecurityUser user) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void verifyPassword(@NotNull User user, String password)  {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        } catch (AuthenticationException exp) {
            throw new WrongPasswordException(WRONG_PASSWORD_ERROR);
        }
    }

    private @NotNull SecurityUser convertToSecurityUser(
            @NotNull UserDetails connectedUser) throws UsernameNotFoundException {
        return (SecurityUser) connectedUser;
    }

    public <T extends OwnByUser> void validateOwnership(
            @NotNull T ownedEntity, UserDetails connectedUser, String errorMsg) {
        User securityUser  = retriveConnectedUser(connectedUser);
        User ownedUser = ownedEntity.getOwner();
        if (!ownedUser.equals(securityUser)) {
            throw new BadCredentialsException(errorMsg);
        }
    }
}
