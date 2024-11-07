package com.andwis.travel_with_anna.trip.day;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Day Controller Tests")
class DayControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private DayFacade facade;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void addDay_ShouldReturnAccepted() throws Exception {
        // Given
        DayAddDeleteRequest request = new DayAddDeleteRequest(1L, true);
        doNothing().when(facade).addDay(request);
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/day/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getDayById_ShouldReturnDayResponse() throws Exception {
        // Given
        DayResponse dayResponse = new DayResponse(
                1L, LocalDate.now(), "Monday", true, 1, null, 1L, List.of());
        when(facade.getDayById(1L)).thenReturn(dayResponse);

        // When & Then
        mockMvc.perform(get("/day/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dayId").value(1))
                .andExpect(jsonPath("$.dayOfWeek").value("Monday"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getDays_ShouldReturnListOfDays() throws Exception {
        // Given
        DayResponse dayResponse = new DayResponse(
                1L, LocalDate.now(), "Monday", true, 1, null, 1L, List.of());
        Mockito.when(facade.getDays(1L)).thenReturn(List.of(dayResponse));

        // When & Then
        mockMvc.perform(get("/day/trip/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayId").value(1))
                .andExpect(jsonPath("$[0].dayOfWeek").value("Monday"));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void generateDays_ShouldReturnAccepted() throws Exception {
        // Given
        DayGeneratorRequest request = new DayGeneratorRequest(
                1L, LocalDate.now(), LocalDate.now().plusDays(5));
        doNothing().when(facade).generateDays(request);
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(post("/day/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void changeDayDate_ShouldReturnAccepted() throws Exception {
        // Given
        DayRequest request = new DayRequest(1L, LocalDate.now());
        doNothing().when(facade).changeDayDate(request);
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(patch("/day/change/date")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void deleteDay_ShouldReturnNoContent() throws Exception {
        // Given
        DayAddDeleteRequest request = new DayAddDeleteRequest(1L, true);
        doNothing().when(facade).deleteDay(request);
        String jsonContent = objectMapper.writeValueAsString(request);

        // When & Then
        mockMvc.perform(delete("/day")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNoContent());
    }
}