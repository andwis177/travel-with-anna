package com.andwis.travel_with_anna.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.json.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Authentication Controller tests")
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService service;

    @Test
    void register_ShouldReturnAccepted() throws Exception {
        //Given
        RegistrationRequest request = new RegistrationRequest("testuser", "test@example.com", "password123");
        String jsonContent = objectMapper.writeValueAsString(request);

        //When
        ResultActions result =
                mockMvc
                        .perform(MockMvcRequestBuilders
                                .post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(jsonContent))
                        .andExpect(status().isAccepted());
        //Then
        assertEquals(202, result.andReturn().getResponse().getStatus());
    }

    @Test
    void register_ShouldReturnBadRequest() throws Exception {
        //Given
        RegistrationRequest request = new RegistrationRequest("test", "test.com", "passwor123");
        String jsonContent = objectMapper.writeValueAsString(request);

        //When
        ResultActions result =
                mockMvc
                        .perform(MockMvcRequestBuilders
                                .post("/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(jsonContent))
                        .andExpect(status().isBadRequest());
        //Then
        assertEquals(400, result.andReturn().getResponse().getStatus());
    }

    @Test
    void authenticate_ShouldReturnOk() throws Exception {
        //Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "password123");
        String jsonContent = objectMapper.writeValueAsString(request);
        AuthenticationResponse response = new AuthenticationResponse("token value");
        when(service.authenticate(request)).thenReturn(response);

        //When
        ResultActions result =
                mockMvc
                        .perform(MockMvcRequestBuilders
                                .post("/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(jsonContent))
                        .andExpect(status().isOk())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.token").exists());

        //Then
        assertEquals(200, result.andReturn().getResponse().getStatus());
        JSONObject jsonResponse = new JSONObject(result.andReturn().getResponse().getContentAsString());
        assertEquals("token value", jsonResponse.getString("token"));
    }

    @Test
    void authenticate_ShouldReturnBadRequest() throws Exception {
        //Given
        AuthenticationRequest request = new AuthenticationRequest("test@example.com", "passwor");
        String jsonContent = objectMapper.writeValueAsString(request);

        //When
        ResultActions result =
                mockMvc
                        .perform(MockMvcRequestBuilders
                                .post("/auth/authenticate")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .content(jsonContent))
                        .andExpect(status().isBadRequest())
                        .andExpect(MockMvcResultMatchers.jsonPath("$.token").doesNotExist());

        //Then
        assertEquals(400, result.andReturn().getResponse().getStatus());
    }

    @Test
    void confirm_ShouldActivateAccount() throws Exception {
        //Given
        String token = "12345";
        doNothing().when(service).activateAccount(token);

        //When
        ResultActions result =
                mockMvc
                        .perform(MockMvcRequestBuilders
                                .get("/auth/activate-account")
                                .param("token", token))
                        .andExpect(status().isOk());

        //Then
        assertEquals(200, result.andReturn().getResponse().getStatus());
    }

    @Test
    void confirm_ShouldReturnMessagingException() throws Exception {
        //Given
        String token = "12345";

        doThrow(new MessagingException("Test messaging exception"))
                .when(service).activateAccount(token);

        //When
        ResultActions result =
                mockMvc
                        .perform(MockMvcRequestBuilders
                                .get("/auth/activate-account")
                                .param("token", token))
                        .andExpect(status().isInternalServerError());
        //Then
        assertEquals(500, result.andReturn().getResponse().getStatus());
        assertEquals("Test messaging exception", Objects.requireNonNull(result.andReturn().getResolvedException()).getMessage());
    }
}