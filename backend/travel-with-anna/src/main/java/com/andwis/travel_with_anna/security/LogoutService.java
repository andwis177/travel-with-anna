package com.andwis.travel_with_anna.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public void logout(
            @NotNull HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        String jwt = extractJwtFromRequest(request);
        if (jwt == null) return;

        SecurityContextHolder.clearContext();
    }

    private @Nullable String extractJwtFromRequest(@NotNull HttpServletRequest request) {
        final String authHeader = request.getHeader(AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(BEARER_PREFIX.length()).trim();
    }
}
