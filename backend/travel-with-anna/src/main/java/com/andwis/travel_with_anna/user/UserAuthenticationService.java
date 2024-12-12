package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.security.OwnableByUser;
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
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public User getConnectedUser(UserDetails connectedUser) {
        var securityUser = getSecurityUser(connectedUser);
        return securityUser.getUser();
    }

    protected void updateSecurityContext(String userName) {
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User is logged out");
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public void verifyPassword(@NotNull User user, String password)  {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        } catch (AuthenticationException exp) {
            throw new WrongPasswordException("Wrong password");
        }
    }

    protected @NotNull SecurityUser getSecurityUser(@NotNull UserDetails connectedUser) throws UsernameNotFoundException {
        return (SecurityUser) connectedUser;
    }

    public <T extends OwnableByUser> void verifyOwner(
            @NotNull T ownedEntity,
            UserDetails connectedUser,
            String message) {
        User securityUser  = getConnectedUser(connectedUser);
        User ownedUser = ownedEntity.getOwner();
        if (!ownedUser.equals(securityUser)) {
            throw new BadCredentialsException(message);
        }
    }
}
