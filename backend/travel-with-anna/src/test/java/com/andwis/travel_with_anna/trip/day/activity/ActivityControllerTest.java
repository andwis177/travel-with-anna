package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.api.country.City;
import com.andwis.travel_with_anna.api.country.Country;
import com.andwis.travel_with_anna.utility.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Activity Controller Tests")
class ActivityControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ActivityFacade facade;
    private AddressDetail addressDetail;
    private MessageResponse messageResponse;
    private ActivityDetailedResponse activityDetailedResponse;
    private ActivityRequest activityRequest;

    @BeforeEach
    void setUp() {
        activityRequest = ActivityRequest.builder()
                .tripId(1L)
                .dateTime("2021-12-12T12:00")
                .endTime("2021-12-12T14:00")
                .activityTitle("Test Activity")
                .badge("Test Badge")
                .type("Test Type")
                .status("Test Status")
                .addressRequest(null)
                .isDayTag(true)
                .build();

        Country country = Country.builder().build();
        City city = City.builder().build();
        addressDetail = new AddressDetail(List.of(), country, List.of(), city);
        messageResponse = new MessageResponse("Activity updated successfully");
        activityDetailedResponse = new ActivityDetailedResponse(addressDetail, List.of(), BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void createActivity_ShouldReturnAcceptedStatus() throws Exception {
        // Given
        doNothing().when(facade).createSingleActivity(any(ActivityRequest.class));
        String jsonContent = objectMapper.writeValueAsString(activityRequest);

        // When & Then
        mockMvc.perform(post("/activity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void createAssociatedActivities_ShouldReturnAcceptedStatus() throws Exception {
        // Given
        ActivityRequest activityRequest2 = ActivityRequest.builder()
                .tripId(1L)
                .dateTime("2021-22-12T13:00")
                .endTime("2022-12-12T16:00")
                .activityTitle("Test Activity 2")
                .badge("Test Badge 2")
                .type("Test Type 2")
                .status("Test Status 2")
                .addressRequest(null)
                .isDayTag(true)
                .build();

        ActivityAssociatedRequest associatedRequest = ActivityAssociatedRequest.builder()
                .firstRequest(activityRequest)
                .secondRequest(activityRequest2)
                .isAddressSeparated(false)
                .build();

        doNothing().when(facade).createAssociatedActivities(any(ActivityAssociatedRequest.class));
        String jsonContent = objectMapper.writeValueAsString(associatedRequest);

        // When & Then
        mockMvc.perform(post("/activity/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void updateActivity_ShouldReturnAcceptedStatusWithMessageResponse() throws Exception {
        // Given
        ActivityUpdateRequest updateRequest = ActivityUpdateRequest.builder()
                .activityId(1L)
                .dayId(1L)
                .oldDate("2023-12-12")
                .newDate("2023-12-14")
                .startTime("12:00")
                .build();

        when(facade.updateActivity(any(ActivityUpdateRequest.class))).thenReturn(messageResponse);
        String jsonContent = objectMapper.writeValueAsString(updateRequest);

        // When & Then
        mockMvc.perform(patch("/activity/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Activity updated successfully"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void fetchActivitiesByDayId_ShouldReturnOkWithActivityDetailedResponse() throws Exception {
        // Given
        when(facade.fetchActivitiesByDayId(any(Long.class))).thenReturn(activityDetailedResponse);

        // When & Then
        mockMvc.perform(get("/activity/day/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.addressDetail").isNotEmpty())
                .andExpect(jsonPath("$.activities").isArray())
                .andExpect(jsonPath("$.totalPrice").value(0))
                .andExpect(jsonPath("$.totalPayment").value(0));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void fetchAddressDetailsByDayId_ShouldReturnOkWithAddressDetail() throws Exception {
        // Given
        when(facade.fetchAddressDetailsByDayId(any(Long.class))).thenReturn(addressDetail);

        // When & Then
        mockMvc.perform(get("/activity/day/1/details")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void fetchAddressDetailsByTripId_ShouldReturnOkWithAddressDetail() throws Exception {
        // Given
        when(facade.fetchAddressDetailsByTripId(any(Long.class))).thenReturn(addressDetail);

        // When & Then
        mockMvc.perform(get("/activity/trip/1/details")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void deleteActivityById_ShouldReturnNoContentStatus() throws Exception {
        // Given
        doNothing().when(facade).deleteActivityById(any(Long.class));

        // When & Then
        mockMvc.perform(delete("/activity/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}