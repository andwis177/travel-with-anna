package com.andwis.travel_with_anna.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

class LogoutServiceTest {
    private LogoutService logoutService;
    @Mock
    private HttpServletRequest mockRequest;
    @Mock
    private HttpServletResponse mockResponse;
    @Mock
    private Authentication mockAuthentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logoutService = new LogoutService();
        SecurityContextHolder.clearContext();
    }

    @Test
    void logout_shouldClearSecurityContext_whenValidJwtIsProvided() {
        // Given
        String validJwt = "validToken";
        when(mockRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + validJwt);

        // When
        logoutService.logout(mockRequest, mockResponse, mockAuthentication);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "SecurityContext should be cleared");
    }

    @Test
    void logout_shouldNotClearSecurityContext_whenNoAuthorizationHeader() {
        // Given
        when(mockRequest.getHeader(AUTHORIZATION)).thenReturn(null);

        // When
        logoutService.logout(mockRequest, mockResponse, mockAuthentication);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "SecurityContext should remain cleared as no valid JWT was provided");
    }

    @Test
    void logout_shouldNotClearSecurityContext_whenAuthorizationHeaderDoesNotStartWithBearer() {
        // Given
        when(mockRequest.getHeader(AUTHORIZATION)).thenReturn("InvalidHeader token");

        // When
        logoutService.logout(mockRequest, mockResponse, mockAuthentication);

        // Then
        assertNull(SecurityContextHolder.getContext().getAuthentication(),
                "SecurityContext should not be cleared with invalid Authorization header");
    }

    @Test
    void extractJwtFromRequest_shouldReturnJwt_whenValidBearerTokenIsProvided() throws Exception {
        // Given
        String validJwt = "validToken";
        when(mockRequest.getHeader(AUTHORIZATION)).thenReturn("Bearer " + validJwt);

        var method = LogoutService.class.getDeclaredMethod("extractJwtFromRequest", HttpServletRequest.class);
        method.setAccessible(true);

        // When
        String extractedJwt = (String) method.invoke(logoutService, mockRequest);

        // Then
        assertEquals(validJwt, extractedJwt, "The extracted JWT should match the valid token");
    }

    @Test
    void extractJwtFromRequest_shouldReturnNull_whenInvalidAuthorizationHeader() throws Exception {
        // Given
        when(mockRequest.getHeader(AUTHORIZATION)).thenReturn("InvalidHeader");
        var method = LogoutService.class.getDeclaredMethod("extractJwtFromRequest", HttpServletRequest.class);
        method.setAccessible(true);

        // When
        String extractedJwt = (String) method.invoke(logoutService, mockRequest);

        // Then
        assertNull(extractedJwt, "JWT should be null for invalid Authorization header");
    }
}
