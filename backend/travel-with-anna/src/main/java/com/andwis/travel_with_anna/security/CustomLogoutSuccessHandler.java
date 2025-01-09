package com.andwis.travel_with_anna.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final int SUCCESS_STATUS_CODE = HttpServletResponse.SC_OK;

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                @NotNull HttpServletResponse response, Authentication authentication)
            throws IOException {
        handleLogoutSuccess(response);
    }

    private void handleLogoutSuccess(@NotNull HttpServletResponse response) throws IOException {
        response.setStatus(CustomLogoutSuccessHandler.SUCCESS_STATUS_CODE);
        response.getWriter().flush();
    }
}
