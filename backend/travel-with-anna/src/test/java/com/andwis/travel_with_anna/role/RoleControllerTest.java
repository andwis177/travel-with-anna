package com.andwis.travel_with_anna.role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.ADMIN;
import static com.andwis.travel_with_anna.role.RoleType.USER;
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

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RoleService roleService() {
            return Mockito.mock(RoleService.class);
        }
        @Bean
        public Authentication connectedUser() {
            return Mockito.mock(Authentication.class);
        }
    }

    @Autowired
    private RoleService service;
    @Autowired
    private Authentication connectedUser;

    @Test
    @WithMockUser(username = "email@example.com", roles = "USER")
    void getAllRoleNames_ShouldReturnOkWithRoleNamesList() throws Exception {
        // Given
        List<String> roles = List.of(USER.getRoleName(), ADMIN.getRoleName());
        when(service.getAllRoleNames()).thenReturn(roles);

        // When
        mockMvc.perform(get("/role/all-names")
                        .principal(connectedUser))
                .andExpect(status().isOk())
                .andExpect(content().json("['USER', 'ADMIN']"));
    }
}