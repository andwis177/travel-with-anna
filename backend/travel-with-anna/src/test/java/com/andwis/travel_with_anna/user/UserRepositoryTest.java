package com.andwis.travel_with_anna.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
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
    private User user;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .userName("userName")
                .email("user@example.com")
                .password("password")
                .build();
       userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void testFindByEmail() {
        // Given
        // When
        User retrivedUser = userRepository.findByEmail("user@example.com").orElse(null);

        // Then
        assertNotNull(retrivedUser);
        assertEquals(user.getUserId(), retrivedUser.getUserId());
        assertEquals("userName", retrivedUser.getUserName());
        assertEquals("user@example.com", retrivedUser.getEmail());
        assertEquals("password", retrivedUser.getPassword());
    }

    @Test
    void testFindByUserName() {
        // Given
        // When
        User retrivedUser = userRepository.findByUserName("userName").orElse(null);

        // Then
        assertNotNull(retrivedUser);
        assertEquals(user.getUserId(), retrivedUser.getUserId());
        assertEquals("userName", retrivedUser.getUserName());
        assertEquals("user@example.com", retrivedUser.getEmail());
        assertEquals("password", retrivedUser.getPassword());
    }

    @Test
    void testExistsByEmail() {
        // Given
        // When
        boolean userExists = userRepository.existsByEmail("user@example.com");

        // Then
        assertTrue(userExists);
    }

    @Test
    void testExistsByUserName() {
        // Given
        // When
        boolean userExists = userRepository.existsByUserName("userName");

        // Then
        assertTrue(userExists);
    }
}