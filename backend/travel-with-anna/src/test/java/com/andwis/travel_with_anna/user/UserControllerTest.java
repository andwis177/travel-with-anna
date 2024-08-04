package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("UserController tests")
class UserControllerTest {

    @Mock
    private UserService service;

    @Mock
    private Authentication connectedUser;

    @InjectMocks
    private UserController userController;


    @Test
    void testGetProfile() {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("email@example.com")
                .userName("username")
                .password("password")
                .build();

        when(service.getProfile(connectedUser)).thenReturn(userCredentials);

        // When
        ResponseEntity<UserCredentials> response = userController.getProfile(connectedUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userCredentials, response.getBody());
        verify(service, times(1)).getProfile(connectedUser);
    }

    @Test
    void testUpdate() {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("email@example.com")
                .userName("username")
                .password("password")
                .build();

        AuthenticationResponse authResponse = AuthenticationResponse.builder()
                .token("token")
                .build();

        when(service.updateUserExecution(userCredentials, connectedUser)).thenReturn(authResponse);

        // When
        ResponseEntity<AuthenticationResponse> response = userController.update(userCredentials, connectedUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
        verify(service, times(1)).updateUserExecution(userCredentials, connectedUser);
    }

    @Test
    void testChangePassword() {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        UserRespond userRespond = UserRespond.builder()
                .message("message")
                .build();

        when(service.changePassword(request, connectedUser)).thenReturn(userRespond);

        // When
        ResponseEntity<UserRespond> response = userController.changePassword(request, connectedUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userRespond, response.getBody());
        verify(service, times(1)).changePassword(request, connectedUser);
    }

    @Test
    void testDelete() {
        // Given
        PasswordRequest request = new PasswordRequest("password");

        UserRespond userRespond = UserRespond.builder()
                .message("message")
                .build();

        when(service.deleteUser(request, connectedUser)).thenReturn(userRespond);

        // When
        ResponseEntity<UserRespond> response = userController.delete(request, connectedUser);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userRespond, response.getBody());
        verify(service, times(1)).deleteUser(request, connectedUser);
    }
}