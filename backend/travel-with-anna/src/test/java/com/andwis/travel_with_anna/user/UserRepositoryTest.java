package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("User Repository tests")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;
    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        roleRepository.save(role);

        user = User.builder()
                .userName("userName")
                .email("user@example.com")
                .password("password")
                .role(role)
                .avatarId(1L)
                .build();
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
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