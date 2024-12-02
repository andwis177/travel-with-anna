package com.andwis.travel_with_anna.security;

import com.andwis.travel_with_anna.handler.exception.JwtParsingException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import io.jsonwebtoken.Claims;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("JWT Service tests")
class JwtServiceTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrivedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testGenerateJwtToken() {
        String jwtToken =  jwtService.generateJwtToken(createAuthentication(user));

        assertNotNull(jwtToken);
        assertFalse(jwtToken.isEmpty());
    }

    @Test
    void testExtractClaim_Success() {
        // Given
        String jwtToken =  jwtService.generateJwtToken(createAuthentication(user));
        Date before = new Date(System.currentTimeMillis() - 1000);
        Date shouldExpireBefore = new Date(System.currentTimeMillis() + jwtExpiration - 1000);

        // When
        String claimResolver = jwtService.extractClaim(jwtToken, Claims::getSubject);
        Date issuedAt = jwtService.extractClaim(jwtToken, Claims::getIssuedAt);
        Date expiration = jwtService.extractClaim(jwtToken, Claims::getExpiration);

        Date after = new Date(System.currentTimeMillis());
        Date shouldExpireAfter = new Date(System.currentTimeMillis() + jwtExpiration);

        // Then
        assertNotNull(claimResolver);
        assertFalse(claimResolver.isEmpty());
        assertEquals("email@example.com", claimResolver);
        assertTrue(issuedAt.before(after));
        assertTrue(issuedAt.after(before));
        assertTrue(expiration.before(shouldExpireAfter));
        assertTrue(expiration.after(shouldExpireBefore));
    }

    @Test
    void testExtractClaim_Failed() {
        // Given
        String jwtToken =  "Invalid token";

        // When & Then
        assertThrows(JwtParsingException.class,
                () -> jwtService.extractClaim(jwtToken, Claims::getSubject));
    }

    @Test
    void testExtractUserName() {
        // Given
        String jwtToken =  jwtService.generateJwtToken(createAuthentication(user));

        // When
        String claimResolver = jwtService.extractUsername(jwtToken);

        // Then
        assertNotNull(claimResolver);
        assertFalse(claimResolver.isEmpty());
        assertEquals("email@example.com", claimResolver);

    }

    @Test
    void testIsTokenValid() {
        // Given
        String jwtToken =  jwtService.generateJwtToken(createAuthentication(user));
        UserDetails userDetails1 = (UserDetails) createAuthentication(user).getPrincipal();
        // When

        boolean isTokenValid = jwtService.isTokenValid(jwtToken, userDetails1);

        // Then
        assertTrue(isTokenValid);
    }

    @Contract("_ -> new")
    private @NotNull Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}