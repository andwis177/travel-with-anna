package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleNameResponse;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.security.OwnByUser;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("User Authentication Service Tests")
class UserAuthenticationServiceTest {

    @Autowired
    private UserAuthenticationService userAuthenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        Role userRole = roleRepository.findByRoleName(USER.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(1, USER.getRoleName(), USER.getAuthority())));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);
        userRepository.save(user);

        User secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .avatarId(2L)
                .build();
        secondaryUser.setEnabled(true);
        userRepository.save(secondaryUser);
    }

    @AfterEach
    void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testRetriveConnectedUser() {
        // Given
        // When
        UserDetails connectedUserDetails = createUserDetails(user);
        User connectedUser = userAuthenticationService.retriveConnectedUser(connectedUserDetails);

        // Then
        assertNotNull(connectedUser);
        assertEquals(user, connectedUser);
    }

    @Test
    void testGetUserRoleName() {
        // Given
        // When
        UserDetails connectedUserDetails = createUserDetails(user);
        RoleNameResponse roleNameResponse = userAuthenticationService.getUserRoleName(connectedUserDetails);

        // Then
        assertNotNull(roleNameResponse);
        assertEquals("USER", roleNameResponse.roleName());
    }

    @Test
    void testUpdateSecurityContext_UserFound() {
        // Given
        String username = user.getEmail();

        // When
        userAuthenticationService.updateSecurityContext(username);

        // Then
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void testUpdateSecurityContext_UserNotFound() {
        // Given
        String username = "nonExistentUser";

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> userAuthenticationService.updateSecurityContext(username));
    }

    @Test
    void testVerifyPassword_SuccessfulAuthentication() {
        // Given
        String password = "password";

        // When & Then
        userAuthenticationService.verifyPassword(user, password);
    }

    @Test
    void testVerifyPassword_FailedAuthentication() {
        // Given
        String password = "wrongPassword";

        // When & Then
        assertThrows(WrongPasswordException.class, () -> userAuthenticationService.verifyPassword(user, password));
    }

    @Test
    void testValidateOwnership_OwnershipValid() {
        // Given
        OwnByUser ownedEntity = mock(OwnByUser.class);
        when(ownedEntity.getOwner()).thenReturn(user);

        // When & Then
        UserDetails connectedUserDetails = createUserDetails(user);
        userAuthenticationService.validateOwnership(ownedEntity, connectedUserDetails, "User does not own the entity");
    }

    @Test
    void testValidateOwnership_OwnershipInvalid() {
        // Given
        OwnByUser ownedEntity = mock(OwnByUser.class);
        User anotherUser = mock(User.class);
        when(ownedEntity.getOwner()).thenReturn(anotherUser);

        // When & Then
        UserDetails connectedUserDetails = createUserDetails(user);
        assertThrows(BadCredentialsException.class, () ->
                userAuthenticationService.validateOwnership(ownedEntity, connectedUserDetails, "User does not own the entity"));
    }

    private @NotNull UserDetails createUserDetails(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return securityUser;
    }
}