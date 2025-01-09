package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.role.RoleNameResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
    private UserFacade facade;
    @MockBean
    private Authentication connectedUser;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getCredentials_ShouldReturnOkWithCredentials() throws Exception {
        // Given
        UserCredentialsResponse userCredentials = new UserCredentialsResponse(
                "email@example.com",
                "email@example.com",
                "User"
        );

        String jsonResponse = objectMapper.writeValueAsString(userCredentials);

        when(facade.getCredentials(any())).thenReturn(userCredentials);
        when(connectedUser.getName()).thenReturn("email@example.com");

        // When & Then
        mockMvc.perform(get("/user/credentials")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void fetchRole_ShouldReturnOkWithRoleName() throws Exception {
        // Given
        RoleNameResponse roleResponse = new RoleNameResponse("User");
        String jsonResponse = objectMapper.writeValueAsString(roleResponse);

        when(facade.fetchUserRoleName(any())).thenReturn(roleResponse);

        // When & Then
        mockMvc.perform(get("/user/role")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonResponse));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void update_ShouldReturnOkWithUpdatedResponse() throws Exception {
        // Given
        UserCredentialsRequest userCredentials = UserCredentialsRequest.builder()
                .email("email@example.com")
                .userName("username")
                .password("newPassword").build();

        String jsonContent = objectMapper.writeValueAsString(userCredentials);
        AuthenticationResponse expectedResponse = AuthenticationResponse.builder()
                .token("token")
                .email("email@example.com")
                .userName("username")
                .build();

        when(facade.updateUserExecution(any(), any()))
                .thenReturn(expectedResponse);
        when(connectedUser.getName()).thenReturn("email@example.com");

        // When & Then
        mockMvc.perform(patch("/user")
                        .principal(connectedUser)
                        .contentType(MediaType.APPLICATION_JSON)
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
        UserResponse expectedResponse = new UserResponse("Password changed successfully");

        when(facade.changePassword(any(), any()))
                .thenReturn(expectedResponse);
        when(connectedUser.getName()).thenReturn("email@example.com");

        // When & Then
        mockMvc.perform(patch("/user/change-password")
                        .principal(connectedUser)
                        .contentType(MediaType.APPLICATION_JSON)
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
        UserResponse expectedResponse = new UserResponse("User deleted successfully");

        when(facade.deleteConnectedUser(any(), any()))
                .thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(delete("/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNoContent())
                .andExpect(content().json("{'message': 'User deleted successfully'}"));
    }
}