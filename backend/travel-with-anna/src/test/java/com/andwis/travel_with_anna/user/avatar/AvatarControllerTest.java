package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.user.UserAvatarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Avatar Controller tests")
class AvatarControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private Authentication authentication;
    @MockBean
    private UserAvatarService userAvatarService;

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void uploadAvatar_ShouldReturnOk() throws Exception {
        // Given
        MockMultipartFile imageFile = new MockMultipartFile(
                "file",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "test image".getBytes());

        // When & Then
        mockMvc.perform(multipart("/avatar")
                        .file(imageFile)
                        .principal(authentication))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "User")
    void getAvatar_ShouldReturnOkWithImageHeaders() throws Exception {
        // Given
        byte[] imageBytes = "test image".getBytes();
        when(userAvatarService.getAvatar(any())).thenReturn(imageBytes);

        // When & Then
        mockMvc.perform(get("/avatar"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.IMAGE_JPEG))
                .andExpect(content().bytes(imageBytes));
    }
}