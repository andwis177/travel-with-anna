package com.andwis.travel_with_anna.trip.note;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Note Controller Tests")
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NoteFacade noteFacade;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testCreateNewNoteForTrip_ShouldReturnAccepted() throws Exception {
        // Given
        NoteForTripRequest request = NoteForTripRequest.builder()
                .tripId(1L)
                .note("Note content")
                .build();

        String requestBody = objectMapper.writeValueAsString(request);

        doNothing().when(noteFacade).createNewNoteForTrip(request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/note/save")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testGetNoteById_ShouldReturnNote() throws Exception {
        // Given
        Long tripId = 1L;
        NoteResponse noteResponse = new NoteResponse("Note content");

        when(noteFacade.getNoteById(tripId)).thenReturn(noteResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/note/{tripId}", tripId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(noteResponse)));
    }
}