package com.andwis.travel_with_anna.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("User Repository tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByEmail() {
        // Given
        User user = User.builder()
                .userName("user")
                .email("user@example.com")
                .password("password")
                .build();
        user = userRepository.save(user);

        // When
        User retrivedUser = userRepository.findByEmail("user@example.com").orElse(null);

        // Then
        assertNotNull(retrivedUser);
        assertEquals(user.getUserId(), retrivedUser.getUserId());
        assertEquals("user@example.com", retrivedUser.getEmail());
        assertEquals("password", retrivedUser.getPassword());
    }

    @Test
    void testFindByUserEmail_Failure() {
        // Given
        // When
        User retrivedUser = userRepository.findByEmail("nonExistingUser@example.com").orElse(null);

        // Then
        assertNull(retrivedUser);
    }
}