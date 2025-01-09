package com.andwis.travel_with_anna.auth;

import com.andwis.travel_with_anna.email.EmailService;
import com.andwis.travel_with_anna.handler.exception.*;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleService;
import com.andwis.travel_with_anna.security.JwtService;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserCredentialsResponse;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import com.andwis.travel_with_anna.user.token.Token;
import com.andwis.travel_with_anna.user.token.TokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import javax.security.auth.login.AccountLockedException;
import java.time.LocalDateTime;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service tests")
class AuthenticationServiceTest {
    @Captor
    private ArgumentCaptor<User> userCaptor;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserService userService;
    @Mock
    private TokenService tokenService;
    @Mock
    private AvatarService avatarService;
    @Mock
    private EmailService emailService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @InjectMocks
    private AuthenticationService authenticationService;
    private static RegistrationRequest request;
    private static User user;
    private static Role role;
    private static Token token;
    private static final LocalDateTime now = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        request = new RegistrationRequest(
                "username",
                "email@example.com",
                "password",
                "password",
                USER.getRoleName()
        );
        role = new Role();
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
                .createdAt(now)
                .expiresAt(now.plusMinutes(10))
                .user(user)
                .build();
    }

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void testRegister_UserNameAlreadyExists() throws RoleNotFoundException {
        // Given
        when(roleService.getRoleByName(USER.getRoleName())).thenReturn(role);
        when(userService.existsByUserName("username")).thenReturn(true);

        // When & Then
        assertThrows(UserExistsException.class, () -> authenticationService.register(request));
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testRegister_EmailAlreadyExists() throws RoleNotFoundException {
        // Given
        when(roleService.getRoleByName(USER.getRoleName())).thenReturn(role);
        when(userService.existsByEmail("email@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserExistsException.class, () -> authenticationService.register(request));
        verify(userService, never()).saveUser(any());
    }

    @Test
    void testRegister_Success() throws MessagingException, RoleNotFoundException, WrongPasswordException {
        //Given
        when(roleService.getRoleByName(USER.getRoleName())).thenReturn(role);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.existsByEmail("email@example.com")).thenReturn(false);
        when(userService.existsByUserName("username")).thenReturn(false);
        doNothing().when(emailService).sendValidationEmail(any(), any(), any(), any());

        //When
        authenticationService.register(request);

        //Then
        verify(avatarService, times(1)).createAvatar(userCaptor.capture());
        verify(emailService, times(1)).sendValidationEmail(
                eq("email@example.com"),
                eq("username"),
                anyString(),
                eq("Account activation")
        );
        User savedUser = userCaptor.getValue();

        assertNotNull(savedUser);
        assertEquals(request.getEmail(), savedUser.getEmail());
        assertEquals(request.getUserName(), savedUser.getUserName());
        assertEquals("encodedPassword", savedUser.getPassword());
    }

    @Test
    void testRegister_RoleNotFound() throws RoleNotFoundException {
        // Given
        when(roleService.getRoleByName(USER.getRoleName())).thenThrow(RoleNotFoundException.class);

        //When & Then
        assertThrows(RoleNotFoundException.class, () -> authenticationService.register(request));
    }

    @Test
    void testRegister_PasswordDoNotMatch() throws RoleNotFoundException {
        //Given
        request.setConfirmPassword("differentPassword");
        when(roleService.getRoleByName(USER.getRoleName())).thenReturn(role);

        // When & Then
        assertThrows(WrongPasswordException.class, () -> authenticationService.register(request));
        verify(userService, times(0)).saveUser(any());
    }

    @Test
    void testAuthenticationWithCredentials_Success() throws WrongPasswordException, AccountLockedException {
        //Given
        AuthenticationRequest request =
                AuthenticationRequest.builder()
                        .email("email@example.com")
                        .password("password")
                        .build();

        Authentication authentication = mock(Authentication.class);

        UserCredentialsResponse userCredentials = new UserCredentialsResponse(
                "email@example.com",
                "username",
                "ROLE_USER"
        );

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateJwtToken(authentication)).thenReturn("jwtToken");
        when(userService.getCredentials("email@example.com")).thenReturn(userCredentials);

        //When
        AuthenticationResponse response = authenticationService.authenticationWithCredentials(request);

        //Then
        assertEquals("jwtToken", response.getToken());
        assertEquals("email@example.com", response.getEmail());
        assertEquals("username", response.getUserName());
    }

    @Test
    void testAuthentication_Failure() {
        //Given
        AuthenticationRequest request =
                AuthenticationRequest.builder()
                        .email("email@example.com")
                        .password("password")
                        .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new AuthenticationException("Authentication failed") {});

        //When & Then
        assertThrows(WrongPasswordException.class,
                () -> authenticationService.authenticationWithCredentials(request));
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userService, never()).getCredentials(anyString());
    }

    @Test
    void testActivateAccount_ValidToken() throws MessagingException {
        //Given
        token.setExpiresAt(now.plusMinutes(15));
        when(tokenService.getByToken("jwtToken")).thenReturn(token);
        when(userService.getUserById(user.getUserId())).thenReturn(user);

        //When
        authenticationService.activateAccount("jwtToken");

        //Then
        assertTrue(user.isEnabled());
        verify(userService, times(1)).saveUser(user);
        verify(tokenService, times(1)).deleteToken(token);
    }

    @Test
    void testActivateAccount_ExpiredToken() {
        //Given
        token.setExpiresAt(now.minusMinutes(5));
        when(tokenService.getByToken("jwtToken")).thenReturn(token);

        // When & Then
        assertThrows(ExpiredTokenException.class, () -> authenticationService.activateAccount("jwtToken"));
        verify(tokenService).deleteToken(token);
    }

    @Test
    void testActivateAccount_UsernameNotFound() {
        //Given
        token.setExpiresAt(now.plusMinutes(15));
        when(tokenService.getByToken("jwtToken")).thenReturn(token);
        when(userService.getUserById(user.getUserId())).thenReturn(null);
        when(userService.getUserById(user.getUserId())).thenThrow(new UsernameNotFoundException("User not found"));

        //When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> authenticationService.activateAccount("jwtToken"));
        assertEquals("User not found", exception.getMessage());
        assertFalse(user.isEnabled());
        verify(userService, never()).saveUser(user);
        verify(tokenService, never()).saveToken(token);
    }

    @Test
    void testResetPasswordWithUserName_Success() throws MessagingException {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("username");
        when(userService.getUserByUserName("username")).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doNothing().when(emailService).sendResetPassword(any(), any(), any(), eq("Password reset"));

        // When
        authenticationService.resetPassword(request);

        // Then
        verify(userService).saveUser(user);
        verify(emailService).sendResetPassword(any(), any(), any(), eq("Password reset"));
    }

    @Test
    void testResetPasswordWithEmail_Success() throws MessagingException {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("mail@example.com");
        when(userService.getUserByEmail("mail@example.com")).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        doNothing().when(emailService).sendResetPassword(any(), any(), any(), eq("Password reset"));

        // When
        authenticationService.resetPassword(request);

        // Then
        verify(userService).saveUser(user);
        verify(emailService).sendResetPassword(any(), any(), any(), eq("Password reset"));
    }

    @Test
    void testResetPasswordWithEmail_Failed() throws MessagingException {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("email@example.com");
        when(userService.getUserByEmail(request.getCredential()))
                .thenThrow(new EmailNotFoundException("User with this email not found"));

        //When & Then
        assertThrows(EmailNotFoundException.class, () -> authenticationService.resetPassword(request));
        verify(emailService, never()).sendResetPassword(any(), any(), any(), eq("Password reset"));
    }

    @Test
    void testResetPasswordWithUserName_Failed() throws MessagingException {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("username");
        when(userService.getUserByUserName(request.getCredential()))
                .thenThrow(new UsernameNotFoundException("User with this user name not found"));

        //When & Then
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.resetPassword(request));
        verify(emailService, never()).sendResetPassword(any(), any(), any(), eq("Password reset"));
    }

    @Test
    @DisplayName("sendActivationCodeByRequest: Success")
    void testSendActivationCodeByRequest_Success() throws MessagingException {
        // Given
        when(userService.getUserByEmail("email@example.com")).thenReturn(user);
        when(tokenService.isTokenExists(user)).thenReturn(false);
        doNothing().when(emailService).sendValidationEmail(any(), any(), any(), any());

        // When
        authenticationService.sendActivationCodeByRequest("email@example.com");

        // Then
        verify(userService, times(1)).getUserByEmail("email@example.com");
        verify(tokenService, times(1)).isTokenExists(user);
        verify(emailService, times(1)).sendValidationEmail(
                eq("email@example.com"),
                eq("username"),
                anyString(),
                eq("Account activation")
        );
    }

    @Test
    @DisplayName("sendActivationCodeByRequest: User already activated")
    void testSendActivationCodeByRequest_UserAlreadyActivated() throws MessagingException {
        // Given
        user.setEnabled(true);
        when(userService.getUserByEmail("email@example.com")).thenReturn(user);

        // When & Then
        UserIsActiveException exception = assertThrows(UserIsActiveException.class, () ->
                authenticationService.sendActivationCodeByRequest("email@example.com"));

        assertEquals("User is already activated", exception.getMessage());
        verify(userService, times(1)).getUserByEmail("email@example.com");
        verify(tokenService, never()).isTokenExists(any());
        verify(emailService, never()).sendValidationEmail(any(), any(), any(), any());
    }

    @Test
    @DisplayName("sendActivationCodeByRequest: Token already exists")
    void testSendActivationCodeByRequest_TokenAlreadyExists() throws MessagingException {
        // Given
        when(userService.getUserByEmail("email@example.com")).thenReturn(user);
        when(tokenService.isTokenExists(user)).thenReturn(true);
        when(tokenService.getByUser(user)).thenReturn(token);

        // When
        authenticationService.sendActivationCodeByRequest("email@example.com");

        // Then
        verify(userService, times(1)).getUserByEmail("email@example.com");
        verify(tokenService, times(1)).isTokenExists(user);
        verify(tokenService, times(1)).deleteToken(token);
        verify(emailService, times(1)).sendValidationEmail(any(), any(), any(), any());
    }
}
