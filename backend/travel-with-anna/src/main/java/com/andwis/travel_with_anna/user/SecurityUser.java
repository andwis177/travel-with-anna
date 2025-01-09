package com.andwis.travel_with_anna.user;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class SecurityUser implements UserDetails {

    private static final boolean DEFAULT_ACCOUNT_NON_EXPIRED = true;
    private static final boolean DEFAULT_CREDENTIALS_NON_EXPIRED = true;

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return createAuthorities(user);
    }

    private @NotNull @Unmodifiable Collection<? extends GrantedAuthority> createAuthorities(@NotNull User user) {
        return Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getRoleAuthority()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return DEFAULT_ACCOUNT_NON_EXPIRED;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !user.isAccountLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() { return DEFAULT_CREDENTIALS_NON_EXPIRED;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
