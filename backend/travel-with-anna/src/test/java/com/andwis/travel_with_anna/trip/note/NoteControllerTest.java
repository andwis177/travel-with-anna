package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.HashSet;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Note Controller Tests")
class NoteControllerTest {
    @MockBean
    private NoteFacade noteFacade;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    private NoteRequest request;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());

        String encodedPassword = passwordEncoder.encode("password");
        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .ownedTrips(new HashSet<>())
                .build();
        user.setEnabled(true);

        request = NoteRequest.builder()
                .entityId(1L)
                .note("Note content")
                .entityType("day")
                .build();

        userDetails = createUserDetails(user);
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void testCreateNewNoteForTrip_ShouldReturnAccepted() throws Exception {
        // Given
        String requestBody = objectMapper.writeValueAsString(request);
        doNothing().when(noteFacade).saveNote(request, userDetails);

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
        when(noteFacade.getNote(eq(entityId), eq(entityType), any())).thenReturn(noteResponse);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/note")
                        .param("entityId", String.valueOf(entityId))
                        .param("entityType", entityType)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(noteResponse)));
    }

    private @NotNull UserDetails createUserDetails(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        securityUser,
                        user.getPassword(),
                        securityUser.getAuthorities()
                )
        );
        return securityUser;
    }
}