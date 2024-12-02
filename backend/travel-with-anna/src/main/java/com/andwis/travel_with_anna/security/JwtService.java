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
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    private final SecretKey key;

    public JwtService() {
        this.key = Jwts.SIG.HS256.key().build();
    }

    public String generateJwtToken(@NotNull Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        Map<String, Object> claims = createClaims(userDetails);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claims(claims)
                .signWith(this.key)
                .compact();
    }

    private @NotNull @Unmodifiable Map<String, Object> createClaims(@NotNull UserDetails userDetails) {
        List<String> authorities = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Map.of(
                "authorities", authorities);
    }

    public <T> T extractClaim(String token, @NotNull Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException exp) {
            System.out.println("JWT parsing failed: " + exp.getMessage());
            throw new JwtParsingException("JWT parsing failed");
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
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}
