package com.andwis.travel_with_anna.trip.trip;

import com.andwis.travel_with_anna.auth.RegistrationRequest;
import com.andwis.travel_with_anna.user.UserService;
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
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Trip Controller tests")
class TripControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private TripService service;
    @MockBean
    private Authentication connectedUser;


    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void register_ShouldReturnAccepted() throws Exception {
    // Given
    Trip trip = Trip
            .builder()
            .tripName("testTrip")
            .build();
    String jsonContent = objectMapper.writeValueAsString(trip);

    // When
    ResultActions result =
            mockMvc
                    .perform(MockMvcRequestBuilders
                            .post("/trip/create")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .content(jsonContent))
                    .andExpect(status().isAccepted());

    // Then
    assertEquals(202, result.andReturn().getResponse().getStatus());
}

}