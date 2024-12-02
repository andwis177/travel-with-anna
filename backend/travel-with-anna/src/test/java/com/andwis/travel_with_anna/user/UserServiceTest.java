package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.EmailNotFoundException;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.handler.exception.UserNotFoundException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.trip.Trip;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("User Service tests")
class UserServiceTest {
    @Autowired
    private UserAuthenticationService userAuthenticationService;
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

        Trip trip = Trip.builder()
                .tripName("tripName")
                .build();

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .ownedTrips(new HashSet<>())
                .build();
        user.setEnabled(true);
        user.addTrip(trip);
        userId = userRepository.save(user).getUserId();

        User secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(2L)
                .build();
        secondaryUser.setEnabled(true);
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
        Long savedUserId = userService.saveUser(user);
        User savedUser = userRepository.findById(savedUserId).orElseThrow();

        //Then
        assertNotNull(savedUserId);
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
        User connectedUser = userAuthenticationService.getConnectedUser(createUserDetails(user));

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
        assertThrows(UserNotFoundException.class, () -> userService.getUserById(2L));
    }

    @Test
    void testGetCredentials() {
        // Given
        // When
        UserCredentialsResponse credentials = userService.getCredentials(user.getEmail());

        // Then
        assertNotNull(credentials);
        assertEquals(user.getEmail(), credentials.email());
        assertEquals(user.getUserName(), credentials.userName());
    }

    @Test
    void testUpdateUserExecution_Success() throws WrongPasswordException {
        // Given
        UserCredentialsRequest userCredentials = UserCredentialsRequest.builder()
                .email("newEmail@example.com")
                .userName("newUserName")
                .password("password")
                .build();

        // When
        AuthenticationResponse response = userService.updateUserExecution(userCredentials, createUserDetails(user));
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
        UserCredentialsRequest userCredentials = UserCredentialsRequest.builder()
                .email("email2@example.com")
                .userName("newUserName")
                .password("password")
                .build();

        // When & Then
        assertThrows(UserExistsException.class, () ->
                userService.updateUserExecution(userCredentials, createUserDetails(user)));
    }

    @Test
    void testUpdateUserExecution_UserExistsByUsername() {
        // Given
        UserCredentialsRequest userCredentials = UserCredentialsRequest.builder()
                .email("email@example.com")
                .userName("userName2")
                .password("password")
                .build();

        // When & Then
        assertThrows(UserExistsException.class, () ->
                userService.updateUserExecution(userCredentials, createUserDetails(user)));
    }

    @Test
    void testUpdateUserExecution_PasswordMismatch() {
        // Given
        UserCredentialsRequest userCredentials = UserCredentialsRequest.builder()
                .email("newEmail@example.com")
                .userName("newUserName")
                .password("wrongPassword")
                .build();

        // When & Then
        assertThrows(WrongPasswordException.class, () ->
                userService.updateUserExecution(userCredentials, createUserDetails(user)));
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

        UserCredentialsRequest userCredentials = UserCredentialsRequest.builder()
                .email("newEmail@example.com")
                .userName("newUserName")
                .password("wrongPassword")
                .build();

        // When & Then
        assertThrows(WrongPasswordException.class, () ->
                userService.updateUserExecution(userCredentials, createUserDetails(testUser)));
    }

    @Test
    void testChangePassword_Success () throws WrongPasswordException {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest(
                "password", "newPassword", "newPassword");

        // When
        UserResponse response = userService.changePassword(request, createUserDetails(user));

        // Then
        assertNotNull(response);
        assertEquals("Password has been changed", response.getMessage());
        assertTrue(passwordEncoder.matches("newPassword", user.getPassword()));
    }

    @Test
    void testChangePassword_WrongPasswordException () {
        // Given
        ChangePasswordRequest request = new ChangePasswordRequest(
                "password", "newPassword", "differentPassword");

        // When & Then
        assertThrows(WrongPasswordException.class, () -> userService.changePassword(request, createUserDetails(user)));
    }

    @Test
    void testDeleteConnectedUser_Success () throws WrongPasswordException {
        // Given
        long usersQty = userRepository.count();
        PasswordRequest request = new PasswordRequest("password");

        // When
        UserResponse response = userService.deleteConnectedUser(request, createUserDetails(user));
        long userQtyAfterDelete = userRepository.count();

        // Then
        assertNotNull(response);
        assertEquals("User userName has been deleted!", response.getMessage());
        assertFalse(userService.existsByEmail("email@example.com"));
        assertEquals(usersQty - 1, userQtyAfterDelete);
    }

    private @NotNull UserDetails createUserDetails(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        securityUser,
                        user.getPassword(),
                        securityUser.getAuthorities()
                )
        );
        return securityUser;
    }
}