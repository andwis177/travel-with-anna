package com.andwis.travel_with_anna.trip.note;

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
    private NoteRequest request;

    @BeforeEach
    void setUp() {
        request = NoteRequest.builder()
                .entityId(1L)
                .note("Note content")
                .entityType("day")
                .build();
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testCreateNewNoteForTrip_ShouldReturnAccepted() throws Exception {
        // Given
        String requestBody = objectMapper.writeValueAsString(request);
        doNothing().when(noteFacade).saveNote(request);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/note")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testGetNote_ShouldReturnNote() throws Exception {
        // Given
        Long entityId = 1L;
        String entityType = "day";
        NoteResponse noteResponse = new NoteResponse(entityId, "Note content");
        when(noteFacade.getNote(entityId, entityType)).thenReturn(noteResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/note")
                        .param("entityId", String.valueOf(entityId))
                        .param("entityType", entityType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(noteResponse)));
    }
}