package com.andwis.travel_with_anna.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@SpringBootTest
@DisplayName("JWT Filter tests")
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private JwtFilter jwtFilter;

    private final UserDetails userDetails = mock(UserDetails.class);

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testDoFilterInternal_WithAuthPath_ShouldProceedFilterChain() throws ServletException, IOException, IOException {
        when(request.getServletPath()).thenReturn("/twa/v1/auth");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithNoAuthHeader_ShouldProceedFilterChain() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/some/other/path");
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithInvalidAuthHeader_ShouldProceedFilterChain() throws ServletException, IOException {
        when(request.getServletPath()).thenReturn("/some/other/path");
        when(request.getHeader(AUTHORIZATION)).thenReturn("InvalidHeader");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testDoFilterInternal_WithValidAuthHeader_AndValidToken_ShouldSetAuthentication() throws ServletException, IOException {
        System.out.println("1: " + SecurityContextHolder.getContext().getAuthentication());

        when(request.getServletPath()).thenReturn("/some/other/path");
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer valid.jwt.token");
        when(jwtService.extractUsername("valid.jwt.token")).thenReturn("user@example.com");


        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid.jwt.token", userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(List.of());

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(1)).loadUserByUsername("user@example.com");

        UsernamePasswordAuthenticationToken authenticationToken =
                (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        assertEquals(userDetails, authenticationToken.getPrincipal());
        System.out.println("2: " + SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testDoFilterInternal_WithValidAuthHeader_AndInvalidToken_ShouldNotSetAuthentication() throws ServletException, IOException {
        System.out.println("3: " + SecurityContextHolder.getContext().getAuthentication());

        when(request.getServletPath()).thenReturn("/some/other/path");
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer invalid.jwt.token");
        when(jwtService.extractUsername("invalid.jwt.token")).thenReturn("user@example.com");

        String extractedUsername = jwtService.extractUsername("invalid.jwt.token");
        System.out.println("Extracted Username: " + extractedUsername);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@example.com")).thenReturn(userDetails);
        when(jwtService.isTokenValid("invalid.jwt.token", userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(userDetailsService, times(1)).loadUserByUsername("user@example.com");
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        System.out.println("4: " + SecurityContextHolder.getContext().getAuthentication());
    }
}