package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.security.JwtService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("User Service tests")
class UserServiceTest {

    @Mock
    private UsernamePasswordAuthenticationToken principal;

    @Mock
    private SecurityContext securityContext;


    @Mock
    private Authentication authentication;

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private SecurityUser securityUser;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        List<Role> roles = new ArrayList<>();
        roles.add(Role.builder().roleName("USER").build());

        user = User.builder()
                .userName("testUsername")
                .email("email@example.com")
                .roles(roles)
                .build();
        SecurityUser securityUserMocked = mock(SecurityUser.class);
        securityUser = new SecurityUser(user);

        when(securityUserMocked.getUsername()).thenReturn("email@example.com");
        when(securityUserMocked.getUser()).thenReturn(user);
        when(principal.getPrincipal()).thenReturn(securityUserMocked);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetProfile_Success() {
        // Given

        // When
        UserCredentials result = userService.getProfile(principal);

        // Then
        assertEquals("email@example.com", result.getEmail());
        assertEquals("testUsername", result.getUserName());
    }

    @Test
    void testGetProfile_UserNotFound() {
        // Given
        when((principal).getPrincipal())
                .thenReturn(null);
        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.getProfile(principal);
        });
    }

    @Test
    public void testUpdateUserExecution() {
        // Given
        UserCredentials userCredentials = UserCredentials
                .builder()
                .email("newemail@example.com")
                .userName("newUsername")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.existsByUserName("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(jwtService.generateJwtToken(any(Authentication.class))).thenReturn("newJwtToken");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(securityUser);

        // When
        AuthenticationResponse response = userService.updateUserExecution(userCredentials, authentication);

        // Then
        assertNotNull(response);
        assertEquals("newJwtToken", response.getToken());
        verify(userRepository, times(1)).save(user);
        verify(jwtService, times(1)).generateJwtToken(any(Authentication.class));
    }

    @Test
    public void testUpdateUserExecution_UserNotFound() {
        // Given
        when(authentication.getPrincipal()).thenReturn(null);

        UserCredentials userCredentials = UserCredentials.builder().build();

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateUserExecution(userCredentials, authentication);
        });
    }


    @Test
    public void testUpdateUserExecution_UsernameAlreadyExists() {
        // Given
        UserCredentials userCredentials = UserCredentials
                .builder()
                .userName("existingUsername")
                .email("newemail@example.com")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(userRepository.existsByUserName("existingUsername")).thenReturn(true);

        // When
        assertThrows(UserExistsException.class, () -> {
            userService.updateUserExecution(userCredentials, authentication);
        });
    }

    @Test
    public void testUpdateUserExecution_EmailAlreadyExists() {
        // Given
        UserCredentials userCredentials = UserCredentials
                .builder()
                .userName("newUsername")
                .email("existingEmail@example.com")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.existsByEmail("existingEmail@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserExistsException.class, () -> {
            userService.updateUserExecution(userCredentials, authentication);
        });
    }

    @Test
    public void testUpdateUserExecution_NoChangesToUser() {
        // Given
        UserCredentials userCredentials = UserCredentials
                .builder()
                .userName("testUsername")
                .email("email@example.com")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(jwtService.generateJwtToken(any(Authentication.class))).thenReturn("newJwtToken");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(securityUser);

        // When
        AuthenticationResponse response = userService.updateUserExecution(userCredentials, authentication);

        // Then
        assertNotNull(response);
        assertEquals("newJwtToken", response.getToken());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, times(1)).generateJwtToken(any(Authentication.class));
    }

    @Test
    public void testUpdateUserExecution_NullUserCredentials() {
        // Given
        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(jwtService.generateJwtToken(any(Authentication.class))).thenReturn("newJwtToken");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(securityUser);

        // When & Then
        assertThrows(NullPointerException.class, () -> {
            userService.updateUserExecution(null, authentication);
        });
    }

    @Test
    public void testUpdateUserExecution_InvalidEmailAndUsername() {
        // Given
        UserCredentials userCredentials = UserCredentials
                .builder()
                .userName("")
                .email("")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(jwtService.generateJwtToken(any(Authentication.class))).thenReturn("newJwtToken");
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(securityUser);

        // When
        AuthenticationResponse response = userService.updateUserExecution(userCredentials, authentication);

        // Then
        assertNotNull(response);
        assertEquals("newJwtToken", response.getToken());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, times(1)).generateJwtToken(any(Authentication.class));
    }

    @Test
    public void testUpdateUserExecution_UpdateSecurityContext_UserLoggedOut() {
        // Given
        UserCredentials userCredentials = UserCredentials
                .builder()
                .email("newemail@example.com")
                .userName("newUsername")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.existsByUserName("newUsername")).thenReturn(false);
        when(userRepository.existsByEmail("newemail@example.com")).thenReturn(false);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(null);

        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.updateUserExecution(userCredentials, authentication);
        });
    }

    @Test
    public void testChangePassword() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When
        UserRespond response = userService.changePassword(request, authentication);

        // Then
        assertNotNull(response);
        assertEquals("Password has been changed", response.getMessage());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testChangePassword_UserNotFound() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("newPassword")
                .build();

        when(authentication.getPrincipal()).thenReturn(null);
        // When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.changePassword(request, authentication);
        });
    }

    @Test
    public void testChangePassword_NewPasswordAndConfirmPasswordDoNotMatch() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("wrongPassword")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(userRepository.save(any(User.class))).thenReturn(user);

        // When & Then
        assertThrows(WrongPasswordException.class, () -> {
            userService.changePassword(request, authentication);
            verify(userRepository, never()).save(any(User.class));
        });
    }

    @Test
    public void testChangePassword_PasswordIncorrect() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .confirmPassword("wrongPassword")
                .build();

        when(authentication.getPrincipal()).thenReturn(securityUser);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);

        // When & Then
        assertThrows(WrongPasswordException.class, () -> {
            userService.changePassword(request, authentication);
            verify(userRepository, never()).save(any(User.class));
        });
    }
}
