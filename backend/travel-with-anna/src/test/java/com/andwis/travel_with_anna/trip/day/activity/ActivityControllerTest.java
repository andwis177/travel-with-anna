package com.andwis.travel_with_anna.trip.day.activity;

import com.andwis.travel_with_anna.address.AddressDetail;
import com.andwis.travel_with_anna.api.country.City;
import com.andwis.travel_with_anna.api.country.Country;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.utility.MessageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.mockito.ArgumentMatchers.*;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    private AddressDetail addressDetail;
    private ActivityDetailedResponse activityDetailedResponse;
    private ActivityRequest activityRequest;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ActivityFacade activityFacade() {
            return Mockito.mock(ActivityFacade.class);
        }
    }

    @Autowired
    private ActivityFacade facade;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(USER.getRoleName());
        role.setRoleAuthority(USER.getAuthority());

        String encodedPassword = passwordEncoder.encode("password");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);

        activityRequest = ActivityRequest.builder()
                .tripId(1L)
                .dateTime("2021-12-12T12:00")
                .endTime("2021-12-12T14:00")
                .activityTitle("Test Activity")
                .badge("Test Badge")
                .type("Test Type")
                .status("Test Status")
                .addressRequest(null)
                .dayTag(true)
                .build();

        Country country = Country.builder().build();
        City city = City.builder().build();
        addressDetail = new AddressDetail(List.of(), country, List.of(), city);
        activityDetailedResponse = new ActivityDetailedResponse(
                addressDetail, List.of(), BigDecimal.ZERO, BigDecimal.ZERO);
    }

    @Test
    @Transactional
    @WithMockUser(username = "email@example.com", authorities = "User")
    void createActivity_ShouldReturnAcceptedStatus() throws Exception {
        // Given
        doNothing().when(facade).createSingleActivity(eq(activityRequest), any());
        String jsonContent = objectMapper.writeValueAsString(activityRequest);

        // When & Then
        mockMvc.perform(post("/activity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated());
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
                .dayTag(true)
                .build();

        ActivityAssociatedRequest associatedRequest = ActivityAssociatedRequest.builder()
                .firstRequest(activityRequest)
                .secondRequest(activityRequest2)
                .addressSeparated(false)
                .build();

        doNothing().when(facade).createAssociatedActivities(eq(associatedRequest), any());
        String jsonContent = objectMapper.writeValueAsString(associatedRequest);

        // When & Then
        mockMvc.perform(post("/activity/associated")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void updateActivity_ShouldReturnAcceptedStatusWithMessageResponse() throws Exception {
        // Given
        ActivityUpdateRequest updateRequest = ActivityUpdateRequest.builder()
                .activityId(1L)
                .dayId(1L)
                .oldActivityDate("2023-12-12")
                .newActivityDate("2023-12-14")
                .startTime("12:00")
                .build();

        when(facade.updateActivity(argThat(argument ->
                argument.getActivityId().equals(updateRequest.getActivityId()) &&
                        argument.getDayId().equals(updateRequest.getDayId()) &&
                        argument.getOldActivityDate().equals(updateRequest.getOldActivityDate()) &&
                        argument.getNewActivityDate().equals(updateRequest.getNewActivityDate()) &&
                        argument.getStartTime().equals(updateRequest.getStartTime())
        ), any()))
                .thenReturn(new MessageResponse("Activity updated successfully"));
        String jsonContent = objectMapper.writeValueAsString(updateRequest);


        // When & Then
        mockMvc.perform(patch("/activity/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andDo(result -> {
                    System.out.println("Response Status: " + result.getResponse().getStatus());
                    System.out.println("Response Content: " + result.getResponse().getContentAsString());
                })
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("Activity updated successfully"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void fetchActivitiesByDayId_ShouldReturnOkWithActivityDetailedResponse() throws Exception {
        // Given
        when(facade.fetchActivitiesByDayId(any(Long.class),any())).thenReturn(activityDetailedResponse);

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
        when(facade.fetchAddressDetailsByDayId(any(Long.class), any())).thenReturn(addressDetail);

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
        when(facade.fetchAddressDetailsByTripId(any(), any())).thenReturn(addressDetail);

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
        doNothing().when(facade).deleteActivityById(any(Long.class), any());

        // When & Then
        mockMvc.perform(delete("/activity/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}