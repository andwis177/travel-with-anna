package com.andwis.travel_with_anna.role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.andwis.travel_with_anna.role.Role.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Role Controller tests")
class RoleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private RoleService service;
    @MockBean
    private Authentication connectedUser;

    @Test
    @WithMockUser(username = "email@example.com", roles = "USER")
    void getAllRoleNames_ShouldReturnOkWithRoleNamesList() throws Exception {
        // Given
        List<String> roles = List.of(getUserRole(), getAdminRole());
        when(service.getAllRoleNames()).thenReturn(roles);

        // When
        mockMvc.perform(get("/role/all-names")
                        .principal(connectedUser))
                .andExpect(status().isOk())
                .andExpect(content().json("['USER', 'ADMIN']"));
    }
}