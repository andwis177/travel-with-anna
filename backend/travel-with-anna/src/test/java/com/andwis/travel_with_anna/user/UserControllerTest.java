package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("User Controller tests")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService service;
    @MockBean
    private Authentication connectedUser;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getCredentials_ShouldReturnOkWithCredentials() throws Exception {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("email@example.com")
                .userName("username")
                .password("password")
                .build();

        when(service.getCredentials("email@example.com")).thenReturn(userCredentials);
        when(connectedUser.getName()).thenReturn("email@example.com");

        // When
        mockMvc.perform(get("/user/credentials")
                        .principal(connectedUser))
                .andExpect(status().isOk())
                .andExpect(content().json(
                        "{'email': 'email@example.com', 'userName': 'username', 'password': 'password'}"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void update_ShouldReturnOkWithUpdatedResponse() throws Exception {
        // Given
        UserCredentials userCredentials = UserCredentials.builder()
                .email("email@example.com")
                .userName("username")
                .password("newPassword").build();

        String jsonContent = objectMapper.writeValueAsString(userCredentials);
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .token("token")
                .email("email@example.com")
                .userName("username")
                .build();

        when(service.updateUserExecution(any(UserCredentials.class), any(Authentication.class)))
                .thenReturn(expectedResponse);
        when(connectedUser.getName()).thenReturn("email@example.com");

        // When
        mockMvc.perform(patch("/user/update")
                        .principal(connectedUser)
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().json("{'token': 'token'}"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void changePassword_ShouldReturnAcceptedWithResponse() throws Exception {
        // Given
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword")
                .build();

        String jsonContent = objectMapper.writeValueAsString(request);
        UserRespond expectedResponse = new UserRespond("Password changed successfully");

        when(service.changePassword(any(ChangePasswordRequest.class), any(Authentication.class)))
                .thenReturn(expectedResponse);
        when(connectedUser.getName()).thenReturn("email@example.com");

        // When
        mockMvc.perform(patch("/user/change-password")
                        .principal(connectedUser)
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isAccepted())
                .andExpect(content().json("{'message': 'Password changed successfully'}"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void delete_ShouldReturnNoContent() throws Exception {
        // Given
        PasswordRequest request = new PasswordRequest("password");
        String jsonContent = objectMapper.writeValueAsString(request);
        UserRespond expectedResponse = new UserRespond("User deleted successfully");

        when(service.deleteConnectedUser(any(PasswordRequest.class), any(Authentication.class)))
                .thenReturn(expectedResponse);

        // When
        mockMvc.perform(delete("/user/delete")
                        .contentType("application/json")
                        .content(jsonContent))
                .andExpect(status().isNoContent())
                .andExpect(content().json("{'message': 'User deleted successfully'}"));
    }
}