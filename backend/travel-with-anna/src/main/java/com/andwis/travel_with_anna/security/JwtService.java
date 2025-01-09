package com.andwis.travel_with_anna.security;

import com.andwis.travel_with_anna.handler.exception.JwtParsingException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String CLAIM_AUTHORITIES = "authorities";

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;

    private final SecretKey jwtSecretKey;

    public JwtService() {
        this.jwtSecretKey = Jwts.SIG.HS256.key().build();
    }

    public String generateJwtToken(@NotNull Authentication authentication) {
        UserDetails authenticatedUser = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = buildClaims(authenticatedUser);

        return Jwts.builder()
                .subject(authenticatedUser.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claims(claims)
                .signWith(this.jwtSecretKey)
                .compact();
    }

    private @NotNull @Unmodifiable Map<String, Object> buildClaims(@NotNull UserDetails authenticatedUser) {
        List<String> authorities = authenticatedUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Map.of(CLAIM_AUTHORITIES, authorities);
    }

    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimResolver) {
        final Claims claims = parseClaimsFromToken(token);
        return claimResolver.apply(claims);
    }

    private Claims parseClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.jwtSecretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            throw new JwtParsingException("Failed to parse JWT: " + e.getMessage());
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, @NotNull UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }
}
