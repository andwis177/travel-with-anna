package com.andwis.travel_with_anna.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("Custom Logout Success Handler Tests")
class CustomLogoutSuccessHandlerTest {

    private CustomLogoutSuccessHandler logoutSuccessHandler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @Mock
    private PrintWriter writer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        logoutSuccessHandler = new CustomLogoutSuccessHandler();
    }

    @Test
    void testOnLogoutSuccess() throws IOException {
        // Given
        when(response.getWriter()).thenReturn(writer);

        // When
        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(writer).flush();
    }
}