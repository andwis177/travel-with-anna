package com.andwis.travel_with_anna.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.mockito.Mockito.*;

@DisplayName("Custom Logout Success Handler Tests")
class CustomLogoutSuccessHandlerTest {
    private LogoutService logoutService;
    private HttpServletRequest request;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        logoutService = new LogoutService();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
    }

    @Test
    void testLogout_withValidAuthorizationHeader() {
        // Given
        String authHeader = "Bearer valid_token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // When
        logoutService.logout(request, response, null);

        // Then
        verify(request, times(1)).getHeader("Authorization");
        assert(SecurityContextHolder.getContext().getAuthentication() == null);
    }

    @Test
    void testLogout_withoutAuthorizationHeader() {
        // Given
        when(request.getHeader("Authorization")).thenReturn(null);

        // When
        logoutService.logout(request, response, null);

        // Then
        verify(request, times(1)).getHeader("Authorization");
        assert(SecurityContextHolder.getContext().getAuthentication() == null);
    }

    @Test
    void testLogout_withInvalidAuthorizationHeader() {
        // Given
        String authHeader = "Invalid_token";
        when(request.getHeader("Authorization")).thenReturn(authHeader);

        // When
        logoutService.logout(request, response, null);

        // Then
        verify(request, times(1)).getHeader("Authorization");
        assert(SecurityContextHolder.getContext().getAuthentication() == null);
    }
}