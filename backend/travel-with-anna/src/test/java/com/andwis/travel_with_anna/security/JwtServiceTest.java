package com.andwis.travel_with_anna.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

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
    private long jwtExpiration;


    @Test
    public void testGenerateJwtToken() {
        //Given
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("email@example.com");

        when(claims.getSubject()).thenReturn("email@example.com");
        when(claims.getExpiration()).thenReturn(new Date(System.currentTimeMillis() + jwtExpiration));

        //When
        String token = jwtService.generateJwtToken(authentication);

        //Then
        assertNotNull(token);
        assertFalse(token.isEmpty());
        verify(authentication, times(1)).getPrincipal();
        verify(userDetails, times(1)).getUsername();
    }

    @Test
    public void testExtractClaim() {
        // Given
        JwtService jwtServiceMock = mock(JwtService.class);
        SecretKey secretKey = Jwts.SIG.HS256.key().build();
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(System.currentTimeMillis() - jwtExpiration);

        claims = Jwts.claims()
                .subject("email@example.com")
                .issuedAt(now)
                .expiration(expiration)
                .build();

        String token = Jwts.builder()
                .claims(claims)
                .signWith(secretKey)
                .compact();

        //When
        Claims claimsMock = mock(Claims.class);
        when(claimsMock.getSubject()).thenReturn("email@example.com");
        when(claimsMock.getIssuedAt()).thenReturn(now);
        when(claimsMock.getExpiration()).thenReturn(expiration);

        when(jwtServiceMock.extractClaim(eq(token), any(Function.class)))
                .thenAnswer(invocation -> {
                    Function<Claims, ?> resolver = invocation.getArgument(1);
                    return resolver.apply(claimsMock);
                });
        String claimResolver = jwtServiceMock.extractClaim(token, Claims::getSubject);

        //Then
        assertNotNull(claimResolver);
        assertFalse(claimResolver.isEmpty());

        verify(jwtServiceMock, times(1)).extractClaim(eq(token), any(Function.class));
    }
}