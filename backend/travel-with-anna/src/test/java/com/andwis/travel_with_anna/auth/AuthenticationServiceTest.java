package com.andwis.travel_with_anna.auth;

import com.andwis.travel_with_anna.email.EmailService;
import com.andwis.travel_with_anna.handler.exception.ExpiredTokenException;
import com.andwis.travel_with_anna.handler.exception.InvalidTokenException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.security.JwtService;
import com.andwis.travel_with_anna.user.Token;
import com.andwis.travel_with_anna.user.TokenRepository;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Authentication Service tests")
class AuthenticationServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
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
    private final String email = "email@example.com";
    private final String password = "password";
    private static final String jwtToken = "jwtToken";
    private static final LocalDateTime now = LocalDateTime.now();
    private final String invalidToken = "invalid token";

    @BeforeAll
    static void setUp() {
        request = new RegistrationRequest("username", "email@example.com", "password");
        role = new Role();
        role.setRoleName("USER");

        user = User.builder()
                .userName("username")
                .email("email@example.com")
                .password("password")
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(role))
                .build();

        token = Token.builder()
                .token(jwtToken)
                .createdAt(now)
                .expiresAt(now.plusMinutes(10))
                .user(user)
                .build();
    }

    @AfterEach
    void afterEach() {
        SecurityContextHolder.clearContext(); // Reset SecurityContextHolder
    }

    @Test
    void testRegister_Success() throws MessagingException, RoleNotFoundException {
        //Given
        when(roleRepository.findByRoleName("USER")).thenReturn(java.util.Optional.of(role));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encodedPassword");

        //When
        authenticationService.register(request);

        //Then
        verify(userRepository, times(1)).save(any());
        verify(emailService, times(1)).sendValidationEmail(
                eq("email@example.com"),
                eq("username"),
                eq(activationUrl),
                anyString(),
                eq("Account activation")
        );
    }

    @Test
    void testRegister_RoleNotFound() {
        // Given
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.empty());

        //When & Then
        assertThrows(RoleNotFoundException.class, () -> authenticationService.register(request));
    }

    @Test
    void testRegister_MessagingException() throws MessagingException {
        //Given
        when(roleRepository.findByRoleName("USER")).thenReturn(Optional.of(role));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        //When
        doThrow(MessagingException.class).when(emailService).sendValidationEmail(
                eq("email@example.com"),
                eq("username"),
                eq(activationUrl),
                anyString(),
                eq("Account activation")
        );

        // Then
        assertThrows(MessagingException.class, () -> authenticationService.register(request));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testAuthentication_Success() {
        //Given
        AuthenticationRequest request =
                AuthenticationRequest.builder()
                        .email(email)
                        .password(password)
                        .build();

        Authentication authentication = mock(Authentication.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateJwtToken(authentication)).thenReturn(jwtToken);

        //When
        AuthenticationResponse response = authenticationService.authenticate(request);

        //Then
        assertNotNull(response);
        assertEquals(jwtToken, response.getToken());
        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService).generateJwtToken(authentication);
    }

    @Test
    void testAuthentication_Failure() {
        //Given
        AuthenticationRequest request =
                AuthenticationRequest.builder()
                        .email(email)
                        .password(password)
                        .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        //When & Then
        BadCredentialsException exception = assertThrows(BadCredentialsException.class, () -> authenticationService.authenticate(request));
        assertEquals("Bad credentials", exception.getMessage());

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtService, never()).generateJwtToken(any(Authentication.class));
    }


    @Test
    void testActivateAccount_ValidToken() throws MessagingException {
        //Given
        token.setExpiresAt(now.plusMinutes(15));
        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(token));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.of(user));

        //When
        authenticationService.activateAccount(jwtToken);

        //Then
        assertTrue(user.isEnabled());
        verify(userRepository, times(1)).save(user);
        assertNotNull(token.getValidatedAt());
        verify(tokenRepository, times(1)).save(token);
    }

    @Test
    void testActivateAccount_InvalidToken() {
        // Given
        when(tokenRepository.findByToken(invalidToken)).thenReturn(Optional.empty());

        //When & Then
        InvalidTokenException exception = assertThrows(InvalidTokenException.class, () -> authenticationService.activateAccount(invalidToken));
        assertEquals("Invalid token", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verify(tokenRepository, never()).save(any(Token.class));
    }

    @Test
    void testActivateAccount_ExpiredToken() {
        //Given
        token.setExpiresAt(now.minusMinutes(15));

        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(token));

        //When & Then
        ExpiredTokenException exception = assertThrows(ExpiredTokenException.class, () -> authenticationService.activateAccount(jwtToken));
        assertEquals("Token expired. New token sent to your email", exception.getMessage());

        assertFalse(user.isEnabled());
        verify(userRepository, never()).save(user);
        assertNull(token.getValidatedAt());
        verify(tokenRepository, never()).save(token);
    }

    @Test
    void testActivateAccount_UsernameNotFound() {
        //Given
        token.setExpiresAt(now.plusMinutes(15));
        when(tokenRepository.findByToken(jwtToken)).thenReturn(Optional.of(token));
        when(userRepository.findById(user.getUserId())).thenReturn(Optional.empty());

        //When & Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> authenticationService.activateAccount(jwtToken));
        assertEquals("User not found", exception.getMessage());

        assertFalse(user.isEnabled());
        verify(userRepository, never()).save(user);
        assertNull(token.getValidatedAt());
        verify(tokenRepository, never()).save(token);
    }
}
