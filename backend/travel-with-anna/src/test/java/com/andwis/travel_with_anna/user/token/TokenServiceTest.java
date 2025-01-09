package com.andwis.travel_with_anna.user.token;

import com.andwis.travel_with_anna.handler.exception.InvalidTokenException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TokenServiceTest {
    @Mock
    private TokenRepository tokenRepository;

    @InjectMocks
    private TokenService tokenService;

    private User user;
    private Token token;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Role role = new Role();
        role.setRoleName(USER.getRoleName());
        role.setRoleAuthority(USER.getAuthority());

        user = User.builder()
                .userName("username")
                .email("email@example.com")
                .password("password")
                .accountLocked(false)
                .enabled(false)
                .role(role)
                .avatarId(1L)
                .build();

        user.setUserId(1L);

        token = Token.builder()
                .token("jwtToken")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
    }

    @Test
    void saveToken_ShouldCallRepositorySave() {
        // Given
        Token token = new Token();
        token.setToken("testToken");

        // When
        tokenService.saveToken(token);

        // Then
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void isTokenExists_ShouldReturnTrue_WhenTokenExists() {
        // Given
        User user = new User();
        when(tokenRepository.existsByUser(user)).thenReturn(true);

        // When
        boolean result = tokenService.isTokenExists(user);

        // Then
        assertTrue(result);
        verify(tokenRepository, times(1)).existsByUser(user);
    }

    @Test
    void isTokenExists_ShouldReturnFalse_WhenTokenDoesNotExist() {
        // Given
        User user = new User();
        when(tokenRepository.existsByUser(user)).thenReturn(false);

        // When
        boolean result = tokenService.isTokenExists(user);

        // Then
        assertFalse(result);
        verify(tokenRepository, times(1)).existsByUser(user);
    }

    @Test
    void getByToken_ShouldReturnToken_WhenFound() {
        // Given
        String tokenStr = "testToken";
        Token token = new Token();
        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(token));

        // When
        Token result = tokenService.getByToken(tokenStr);

        // Then
        assertNotNull(result);
        assertEquals(token, result);
        verify(tokenRepository, times(1)).findByToken(tokenStr);
    }

    @Test
    void getByToken_ShouldReturnEmpty_WhenNotFound() {
        // Given
        String tokenStr = "testToken";
        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.empty());

        // When
        assertThrows(InvalidTokenException.class, () -> tokenService.getByToken(tokenStr));

        // Then
        verify(tokenRepository, times(1)).findByToken(tokenStr);
    }

    @Test
    void getByUser_ShouldReturnToken() {
        // Given
        when(tokenRepository.findByUser(user)).thenReturn(Optional.ofNullable(token));

        // When
        Token result = tokenService.getByUser(user);

        // Then
        assertNotNull(result);
        assertEquals(token, result);
        verify(tokenRepository, times(1)).findByUser(user);
    }

    @Test
    void getByUser_ShouldReturnEmpty_WhenNotFound() {
        // Given
        when(tokenRepository.findByUser(user)).thenReturn(Optional.empty());

        // When
        assertThrows(InvalidTokenException.class, () -> tokenService.getByUser(user));

        // Then
        verify(tokenRepository, times(1)).findByUser(user);
    }

    @Test
    void getExpiredTokens_ShouldReturnExpiredTokens() {
        // Given
        Token expiredToken = new Token();
        expiredToken.setExpiresAt(LocalDateTime.now().minusDays(2));

        Token validToken = new Token();
        validToken.setExpiresAt(LocalDateTime.now().plusDays(1));

        List<Token> tokens = Arrays.asList(expiredToken, validToken);
        when(tokenRepository.findAll()).thenReturn(tokens);

        // When
        Set<Token> result = tokenService.getExpiredTokens();

        // Then
        assertEquals(1, result.size());
        assertTrue(result.contains(expiredToken));
        assertFalse(result.contains(validToken));
        verify(tokenRepository, times(1)).findAll();
    }

    @Test
    void deleteToken_ShouldCallRepositoryDelete() {
        // Given
        Token token = new Token();

        // When
        tokenService.deleteToken(token);

        // Then
        verify(tokenRepository, times(1)).delete(token);
    }

    @Test
    void deleteExpiredTokens_ShouldDeleteExpiredTokens() {
        // Given
        Token expiredToken = new Token();
        expiredToken.setExpiresAt(LocalDateTime.now().minusDays(1));

        List<Token> tokens = List.of(expiredToken);
        when(tokenRepository.findAll()).thenReturn(tokens);

        // When
        tokenService.deleteExpiredTokens();

        // Then
        verify(tokenRepository, times(1)).deleteAll(anySet());
    }
}
