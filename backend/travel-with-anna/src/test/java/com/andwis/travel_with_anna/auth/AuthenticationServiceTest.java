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
import com.andwis.travel_with_anna.user.token.TokenRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
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
    private TokenRepository tokenRepository;
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
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
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
                getUserRole()
        );
        role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());

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
    void testRegister_Success() throws MessagingException, RoleNotFoundException, WrongPasswordException {
        //Given
        when(roleService.getRoleByName(getUserRole())).thenReturn(role);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");
        when(userService.existsByEmail("email@example.com")).thenReturn(false);
        when(userService.existsByUserName("username")).thenReturn(false);
        doNothing().when(emailService).sendValidationEmail(any(), any(), any(), any(), any());

        //When
        authenticationService.register(request);

        //Then
        verify(avatarService, times(1)).createAvatar(userCaptor.capture());
        verify(emailService, times(1)).sendValidationEmail(
                eq("email@example.com"),
                eq("username"),
                eq(activationUrl),
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
        when(roleService.getRoleByName(getUserRole())).thenThrow(RoleNotFoundException.class);

        //When & Then
        assertThrows(RoleNotFoundException.class, () -> authenticationService.register(request));
    }

    @Test
    void testRegister_UserExistsByEmail() throws RoleNotFoundException {
        //Given
        when(roleService.getRoleByName(getUserRole())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.existsByEmail("email@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserExistsException.class, () -> authenticationService.register(request));
        verify(userService, times(0)).saveUser(any());
    }

    @Test
    void testRegister_UserExistsByUserName() throws RoleNotFoundException {
        //Given
        when(roleService.getRoleByName(getUserRole())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userService.existsByUserName("username")).thenReturn(true);

        // When & Then
        assertThrows(UserExistsException.class, () -> authenticationService.register(request));
        verify(userService, times(0)).saveUser(any());
    }

    @Test
    void testRegister_PasswordDoNotMatch() throws RoleNotFoundException {
        //Given
        request.setConfirmPassword("differentPassword");
        when(roleService.getRoleByName(getUserRole())).thenReturn(role);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // When & Then
        assertThrows(WrongPasswordException.class, () -> authenticationService.register(request));
        verify(userService, times(0)).saveUser(any());
    }

    @Test
    void testAuthenticationWithCredentials_Success() throws WrongPasswordException {
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
        assertNotNull(response);
        assertEquals("jwtToken", response.getToken());
        assertEquals("email@example.com", response.getEmail());
        assertEquals("username", response.getUserName());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateJwtToken(authentication);
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
        when(tokenRepository.findByToken("jwtToken")).thenReturn(Optional.of(token));
        when(userService.getUserById(user.getUserId())).thenReturn(user);

        //When
        authenticationService.activateAccount("jwtToken");

        //Then
        assertTrue(user.isEnabled());
        verify(userService, times(1)).saveUser(user);
        verify(tokenRepository, times(1)).delete(token);
    }

    @Test
    void testActivateAccount_InvalidToken() {
        // Given
        when(tokenRepository.findByToken("invalid token")).thenReturn(Optional.empty());

        //When & Then
        InvalidTokenException exception = assertThrows(InvalidTokenException.class,
                () -> authenticationService.activateAccount("invalid token"));

        assertEquals("Invalid token", exception.getMessage());
        verify(userService, never()).saveUser(any(User.class));
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testActivateAccount_ExpiredToken() {
        //Given
        token.setExpiresAt(now.minusMinutes(25));
        when(tokenRepository.findByToken("jwtToken")).thenReturn(Optional.of(token));

        //When & Then
        ExpiredTokenException exception = assertThrows(ExpiredTokenException.class,
                () -> authenticationService.activateAccount("jwtToken"));
        assertEquals("Token expired. New token was sent to your email", exception.getMessage());

        assertFalse(user.isEnabled());
        verify(userService, never()).saveUser(user);
        verify(tokenRepository, never()).save(token);
    }

    @Test
    void testActivateAccount_UsernameNotFound() {
        //Given
        token.setExpiresAt(now.plusMinutes(15));
        when(tokenRepository.findByToken("jwtToken")).thenReturn(Optional.of(token));
        when(userService.getUserById(user.getUserId())).thenReturn(null);
        when(userService.getUserById(user.getUserId())).thenThrow(new UsernameNotFoundException("User not found"));

        //When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> authenticationService.activateAccount("jwtToken"));
        assertEquals("User not found", exception.getMessage());
        assertFalse(user.isEnabled());
        verify(userService, never()).saveUser(user);
        verify(tokenRepository, never()).save(token);
    }

    @Test
    void testResetPasswordWithUserName_Success() throws MessagingException {
        // Given
        String username = "username";
        String email = "email@example.com";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setUserName(username);

        ResetPasswordRequest request = new ResetPasswordRequest(username);

        when(userService.getUserByUserName(username)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        doNothing().when(emailService).sendResetPassword(any(), any(), any(), any(), eq("Password reset"));

        // When
        authenticationService.resetPassword(request);

        // Then
        verify(userService, times(1)).saveUser(user);
        verify(emailService, times(1))
                .sendResetPassword(any(), any(), any(), any(), eq("Password reset"));
    }

    @Test
    void testResetPasswordWithEmail_Success() throws MessagingException {
        // Given
        String username = "username";
        String email = "email@example.com";
        String encodedPassword = "encodedPassword";
        User user = new User();
        user.setEmail(email);
        user.setUserName(username);

        ResetPasswordRequest request = new ResetPasswordRequest(email);

        when(userService.getUserByEmail(email)).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        doNothing().when(emailService).sendResetPassword(any(), any(), any(), any(), eq("Password reset"));

        // When
        authenticationService.resetPassword(request);

        // Then
        verify(userService, times(1)).saveUser(user);
        verify(emailService, times(1))
                .sendResetPassword(any(), any(), any(), any(), eq("Password reset"));
    }

    @Test
    void testResetPasswordWithEmail_Failed() throws MessagingException {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("email@example.com");
        when(userService.getUserByEmail(request.getCredential()))
                .thenThrow(new EmailNotFoundException("User with this email not found"));

        //When & Then
        assertThrows(EmailNotFoundException.class, () -> authenticationService.resetPassword(request));
        verify(emailService, never()).sendResetPassword(any(), any(), any(), any(), eq("Password reset"));
    }

    @Test
    void testResetPasswordWithUserName_Failed() throws MessagingException {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("username");
        when(userService.getUserByUserName(request.getCredential()))
                .thenThrow(new UsernameNotFoundException("User with this user name not found"));

        //When & Then
        assertThrows(UsernameNotFoundException.class, () -> authenticationService.resetPassword(request));
        verify(emailService, never()).sendResetPassword(any(), any(), any(), any(), eq("Password reset"));
    }
}
