package com.andwis.travel_with_anna.security;

import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@DisplayName("JWT Service tests")
class JwtServiceTest {
    @Mock
    private Authentication authentication;
    @Mock
    private UserDetails userDetails;
    @Mock
    private Claims claims;
    @InjectMocks
    private JwtService jwtService;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration ;

    @Test
    public void testGenerateJwtToken() {
        //Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testUser");

        when(claims.getSubject()).thenReturn("testUser");
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + jwtExpiration));

        //When
        String token = jwtService.generateJwtToken(authentication);

        //Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        verify(authentication, times(1)).getPrincipal();
        verify(userDetails, times(1)).getUsername();
    }
}