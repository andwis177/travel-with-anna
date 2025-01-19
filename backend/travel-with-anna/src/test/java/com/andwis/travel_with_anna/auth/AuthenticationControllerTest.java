package com.andwis.travel_with_anna.auth;

import com.andwis.travel_with_anna.handler.ErrorCodes;
import com.andwis.travel_with_anna.handler.ExceptionResponse;
import com.andwis.travel_with_anna.handler.exception.EmailNotFoundException;
import com.andwis.travel_with_anna.handler.exception.InvalidTokenException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.HashSet;
import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Authentication Controller tests")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AuthenticationService service() {
            return Mockito.mock(AuthenticationService.class);
        }
        @Bean
        public AuthenticationManager authenticationManager() {
            return Mockito.mock(AuthenticationManager.class);
        }
    }

    @Autowired
    private AuthenticationService service;
    @Autowired
    private AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = Role.builder()
                .roleName(USER.getRoleName())
                .roleAuthority(USER.getAuthority())
                .build();
        roleRepository.save(role);

        User user = User.builder()
                .userName("username")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .enabled(true)
                .build();
        userRepository.save(user);
    }

    @Test
    void register_ShouldReturnAccepted() throws Exception {
        // Given
        RegistrationRequest request = new RegistrationRequest(
                "testUser",
                "test@example.com",
                "password123",
                "password123",
                USER.getRoleName()
        );

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        // Then
        verify(service, times(1)).register(any(RegistrationRequest.class));
    }

    @Test
    void register_ShouldReturnBadRequest() throws Exception {
        // Given
        RegistrationRequest request = new RegistrationRequest(
                "test",
                "invalidEmail",
                "password123",
                "password123",
                USER.getRoleName()
        );

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void authenticationWithCredentials_ShouldReturnOk() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest(
                "test@example.com",
                "password123"
        );

        AuthenticationResponse response = new AuthenticationResponse(
                "token value",
                "",
                "",
                ""
        );

        when(service.authenticationWithCredentials(eq(request))).thenReturn(response);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.token").value("token value"));
    }

    @Test
    void authenticationWithCredentials_ShouldReturnBadRequest() throws Exception {
        // Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "passwor");

        when(service.authenticationWithCredentials(any(AuthenticationRequest.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void confirm_ShouldActivateAccount() throws Exception {
        // Given
        String token = "validToken";

        // When
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/auth/activate-account")
                        .param("token", token))
                .andExpect(status().isAccepted());

        // Then
        verify(service, times(1)).activateAccount(token);
    }

    @Test
    void confirm_ShouldReturnMessagingException() throws Exception {
        // Given
        String token = "invalidToken";

        doThrow(new InvalidTokenException("Test messaging exception"))
                .when(service).activateAccount(token);

        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCodes.INVALID_TOKEN.getCode(),
                List.of("Test messaging exception"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/auth/activate-account")
                        .param("token", token))
                .andExpect(status().isUnauthorized())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void restPassword_ShouldResetPassword() throws Exception {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("email@example.com");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        // Then
        verify(service, times(1)).resetPassword(any(ResetPasswordRequest.class));
    }

    @Test
    void restPasswordWithEmail_ShouldReturnEmailNotFoundException() throws Exception {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("email@example.com");

        doThrow(new EmailNotFoundException("User with this email not found"))
                .when(service).resetPassword(any(ResetPasswordRequest.class));

        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCodes.USER_NOT_FOUND.getCode(),
                List.of("User with this email not found"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    void restPasswordWithUserName_ShouldReturnUserNameNotFoundException() throws Exception {
        // Given
        ResetPasswordRequest request = new ResetPasswordRequest("username");

        doThrow(new UsernameNotFoundException("User with this user name not found"))
                .when(service).resetPassword(any(ResetPasswordRequest.class));

        ExceptionResponse exceptionResponse = new ExceptionResponse(ErrorCodes.USER_NOT_FOUND.getCode(),
                List.of("User with this user name not found"));

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string(objectMapper.writeValueAsString(exceptionResponse)));
    }

    @Test
    void resendActivationCode_ShouldReturnOk() throws Exception {
        // Given
        String email = "test@example.com";

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/auth/resend-activation-code")
                        .param("email", email))
                .andExpect(status().isOk());

        verify(service, times(1)).sendActivationCodeByRequest(email);
    }
}