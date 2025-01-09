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

import static org.mockito.Mockito.*;

@DisplayName("Custom Logout Success Handler Tests")
class CustomLogoutSuccessHandlerTest {

    private CustomLogoutSuccessHandler logoutSuccessHandler;

    @Mock
    private HttpServletRequest mockRequest;

    @Mock
    private HttpServletResponse mockResponse;

    @Mock
    private Authentication mockAuthentication;

    @Mock
    private PrintWriter mockWriter;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        logoutSuccessHandler = new CustomLogoutSuccessHandler();

        when(mockResponse.getWriter()).thenReturn(mockWriter);
    }

    @Test
    void onLogoutSuccess_shouldSetResponseStatusToOk() throws IOException {
        // Given
        // When
        logoutSuccessHandler.onLogoutSuccess(mockRequest, mockResponse, mockAuthentication);

        // Then
        verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        verify(mockResponse.getWriter()).flush();
    }

    @Test
    void onLogoutSuccess_shouldHandleIOException() throws IOException {
        // Given
        doThrow(new IOException("Writer error")).when(mockResponse).getWriter();

        // When & Then
        try {
            logoutSuccessHandler.onLogoutSuccess(mockRequest, mockResponse, mockAuthentication);
        } catch (IOException e) {
            verify(mockResponse).setStatus(HttpServletResponse.SC_OK);
        }
    }
}
