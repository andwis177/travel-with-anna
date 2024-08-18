package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.user.token.Token;
import com.andwis.travel_with_anna.user.token.TokenRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Token Repository tests")
class TokenRepositoryTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        tokenRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testFindByToken() {
        // Given
        User user = User.builder()
                .userName("user")
                .email("user@example.com")
                .password("password")
                .avatarId(1L)
                .build();

        userRepository.save(user);

        Token token = Token.builder()
                .token("token")
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .build();
        token.setUser(user);

        Long tokenId = tokenRepository.save(token).getTokenId();

        // When
        Token retrivedToken = tokenRepository.findByToken(token.getToken()).orElse(null);

        // Then
        assertNotNull(retrivedToken);
        assertEquals(tokenId, retrivedToken.getTokenId());
        assertEquals("token", token.getToken());
    }

    @Test
    void testFindByToken_Failure() {
        // Given
        // When
        Token retrivedToken = tokenRepository.findByToken("token").orElse(null);

        // Then
        assertNull(retrivedToken);
    }
}