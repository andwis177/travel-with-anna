package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.andwis.travel_with_anna.role.Role.getAdminRole;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Mapper tests")
class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
    }

    @Test
    void toUserForAdminResponse_withValidData() {
        // Given
        Role role = new Role();
        role.setRoleName(getAdminRole());

        User user = User.builder()
                .userId(1L)
                .userName("TestUser")
                .email("testuser@example.com")
                .accountLocked(false)
                .enabled(true)
                .createdDate(LocalDateTime.of(2023, 1, 1, 12, 0))
                .lastModifiedDate(LocalDateTime.of(2023, 1, 2, 12, 0))
                .role(role)
                .avatarId(100L)
                .build();

        Map<Long, byte[]> avatarsWithUsersId = new HashMap<>();
        avatarsWithUsersId.put(100L, "avatarImage".getBytes());

        // When
        UserAdminResponse response = userMapper.toUserForAdminView(user, avatarsWithUsersId);

        // Then
        assertNotNull(response);
        assertEquals(1L, response.userId());
        assertEquals("TestUser", response.userName());
        assertEquals("testuser@example.com", response.email());
        assertFalse(response.accountLocked());
        assertTrue(response.enabled());
        assertEquals("2023-01-01", response.createdDate().toString());
        assertEquals("2023-01-02", response.lastModifiedDate().toString());
        assertEquals(getAdminRole(), response.roleName());
        assertArrayEquals("avatarImage".getBytes(), response.avatar());
    }

    @Test
    void toUserForAdminResponse_withMissingAvatar() {
        // Given
        Role role = new Role();
        role.setRoleName(getUserRole());

        User user = User.builder()
                .userId(2L)
                .userName("AnotherUser")
                .email("anotheruser@example.com")
                .accountLocked(true)
                .enabled(false)
                .createdDate(LocalDateTime.of(2023, 1, 5, 15, 0))
                .lastModifiedDate(LocalDateTime.of(2023, 1, 6, 15, 0))
                .role(role)
                .avatarId(200L) // ID does not exist in the map
                .build();

        Map<Long, byte[]> avatarsWithUsersId = new HashMap<>();

        // When
        UserAdminResponse response = userMapper.toUserForAdminView(user, avatarsWithUsersId);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.userId());
        assertEquals("AnotherUser", response.userName());
        assertEquals("anotheruser@example.com", response.email());
        assertTrue(response.accountLocked());
        assertFalse(response.enabled());
        assertEquals("2023-01-05", response.createdDate().toString());
        assertEquals("2023-01-06", response.lastModifiedDate().toString());
        assertEquals(getUserRole(), response.roleName());
        assertNull(response.avatar());
    }
}