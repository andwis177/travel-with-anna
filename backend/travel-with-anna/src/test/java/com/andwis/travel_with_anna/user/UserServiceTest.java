package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.EmailNotFoundException;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.handler.exception.UserIdNotFoundException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("User Service tests")
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    private User user;
    private Role retrivedRole;
    private Long userId;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        retrivedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userId = userRepository.save(user).getUserId();

        User secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(2L)
                .build();
        user.setEnabled(true);
        userRepository.save(secondaryUser);
    }

    @AfterEach()
    void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testSaveUser() {
        //Given
        User user = User.builder()
                .userName("testUser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .build();
        user.setEnabled(true);

        // When
        User savedUser = userService.saveUser(user);

        //Then
        assertNotNull(savedUser.getUserId());
        assertEquals("testUser", savedUser.getUserName());
        assertEquals("test@example.com", savedUser.getEmail());
    }

    @Test
    void testExistsByEmail() {
        // Given
        // When
        boolean exists = userService.existsByEmail("email@example.com");

        // Then
        assertTrue(exists);
    }

    @Test
    void testExistsByUserName() {
        // Given
        // When
        boolean exists = userService.existsByUserName(user.getUserName());

        // Then
        assertTrue(exists);
    }

    @Test
    void testGetConnectedUser() {
        // Given
        // When
        User connectedUser = userService.getConnectedUser(createAuthentication(user));

        // Then
        assertNotNull(connectedUser);
        assertEquals(user, connectedUser);
    }

    @Test
    void testGetUserByEmail_UserExists() {
        // Given
        // When
        User foundUser = userService.getUserByEmail("email@example.com");

        // Then
        assertNotNull(foundUser);
        assertEquals("email@example.com", foundUser.getEmail());
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
        // Given
        String email = "nonexistent@example.com";

        // When & Then
        assertThrows(EmailNotFoundException.class, () -> userService.getUserByEmail(email));
    }

    @Test
    void testGetUserByUserName_UserExists() {
        // Given
        // When
        User foundUser = userService.getUserByUserName("userName");

        // Then
        assertNotNull(foundUser);
        assertEquals("userName", foundUser.getUserName());
    }

    @Test
    void testGetUserByUserName_UserNotFound() {
        // Given
        String userName = "nonexistentUser";

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userService.getUserByUserName(userName));
    }

    @Test
    void testGetUserById_UserExists() {
        // Given
        // When
        User foundUser = userService.getUserById(userId);

        // Then
        assertNotNull(foundUser);
        assertEquals("userName", foundUser.getUserName());
    }

    @Test
    void testGetUserById_UserNotFound() {
        // Given & When & Then
        assertThrows(UserIdNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void testGetCredentials() {
        // Given
        // When
        UserCredentials credentials = userService.getCredentials(user.getEmail());

        // Then
        assertNotNull(credentials);
        assertEquals(user.getEmail(), credentials.getEmail());
        assertEquals(user.getUserName(), credentials.getUserName());
    }

    @Test
    void testUpdateUserExecution_Success() {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("newEmail@example.com")
                .userName("newUserName")
                .password("password")
                .build();

        // When
        AuthenticationResponse response = userService.updateUserExecution(userCredentials, createAuthentication(user));
        User retrivedUser = userService.getUserById(userId);

        // Then
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals(response.getEmail(), userCredentials.getEmail());
        assertEquals(response.getUserName(), userCredentials.getUserName());
        assertEquals("newEmail@example.com", retrivedUser.getEmail());
        assertEquals("newUserName", retrivedUser.getUserName());
    }

    @Test
    void testUpdateUserExecution_UserExistsByEmail() {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("email2@example.com")
                .userName("newUserName")
                .password("password")
                .build();

        // When & Then
        assertThrows(UserExistsException.class, () ->
                userService.updateUserExecution(userCredentials, createAuthentication(user)));
    }

    @Test
    void testUpdateUserExecution_UserExistsByUsername() {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("email@example.com")
                .userName("userName2")
                .password("password")
                .build();

        // When & Then
        assertThrows(UserExistsException.class, () ->
                userService.updateUserExecution(userCredentials, createAuthentication(user)));
    }

    @Test
    void testUpdateUserExecution_PasswordMismatch() {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("newEmail@example.com")
                .userName("newUserName")
                .password("wrongPassword")
                .build();

        // When & Then
        assertThrows(BadCredentialsException.class, () ->
                userService.updateUserExecution(userCredentials, createAuthentication(user)));
    }

    @Test
    void testUpdateUserExecution_NotExistingUser() {
        // Given
        User testUser = User.builder()
                .userName("testUser")
                .email("test@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .build();
        testUser.setEnabled(true);

        UserCredentials userCredentials = UserCredentials.builder()
                .email("newEmail@example.com")
                .userName("newUserName")
                .password("wrongPassword")
                .build();

        // When & Then
        assertThrows(BadCredentialsException.class, () ->
                userService.updateUserExecution(userCredentials, createAuthentication(testUser)));
    }

    @Test
    void testChangePassword_Success () throws RoleNotFoundException {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest("password", "newPassword", "newPassword");

        // When
        UserRespond response = userService.changePassword(request, createAuthentication(user));

        // Then
        assertNotNull(response);
        assertEquals("Password has been changed", response.getMessage());
        assertTrue(passwordEncoder.matches("newPassword", user.getPassword()));
    }

    @Test
    void testChangePassword_WrongPasswordException () {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest("password", "newPassword", "differentPassword");

        // When & Then
        assertThrows(WrongPasswordException.class, () -> userService.changePassword(request, createAuthentication(user)));
    }

    @Test
    void testDeleteConnectedUser_Success () {
        // Given
        long usersQty = userRepository.count();
        PasswordRequest request = new PasswordRequest("password");

        // When
        UserRespond response = userService.deleteConnectedUser(request, createAuthentication(user));
        long userQtyAfterDelete = userRepository.count();

        // Then
        assertNotNull(response);
        assertEquals("User userName has been deleted!", response.getMessage());
        assertFalse(userService.existsByEmail("email@example.com"));
        assertEquals(usersQty - 1, userQtyAfterDelete);
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}