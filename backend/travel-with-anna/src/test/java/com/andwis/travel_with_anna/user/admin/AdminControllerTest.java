package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.utility.PageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("AdminController tests")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private AdminService adminService;


    private User user;

    @BeforeEach
    void setup() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrivedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void testGetAllUsers() throws Exception {
        // Getter
        UserAdminView userResponse = UserAdminView.builder()
                .userId(1L)
                .userName("TestUser")
                .email("testuser@example.com")
                .accountLocked(false)
                .enabled(true)
                .createdDate(LocalDate.of(2024, 8, 19))
                .lastModifiedDate(LocalDate.of(2024, 8, 19))
                .roleName("USER")
                .cover(null)
                .build();

        PageResponse<UserAdminView> response = new PageResponse<>(
                List.of(userResponse),
                0, 10, 1, 1, true, true
        );

        when(adminService.getAllUsers(anyInt(), anyInt(), any(Authentication.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(createAuthentication(user))) // Simulate an authenticated user
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "content": [
                            {
                                "userId": 1,
                                "userName": "TestUser",
                                "email": "testuser@example.com",
                                "accountLocked": false,
                                "enabled": true,
                                "createdDate": "2024-08-19",
                                "lastModifiedDate": "2024-08-19",
                                "roleName": "USER",
                                "cover": null
                            }
                        ],
                        "number": 0,
                        "size": 10,
                        "totalElements": 1,
                        "totalPages": 1,
                        "first": true,
                        "last": true
                    }
                    """));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void testGetAllUsersWithDefaultParams() throws Exception {
        // Given
        when(adminService.getAllUsers(anyInt(), anyInt(), any(Authentication.class)))
                .thenReturn(new PageResponse<>(List.of(), 0, 10, 0, 0, true, true));

        // When & Then
        mockMvc.perform(get("/admin/users")
                        .principal(createAuthentication(user)))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "content": [],
                        "number": 0,
                        "size": 10,
                        "totalElements": 0,
                        "totalPages": 0,
                        "first": true,
                        "last": true
                    }
                    """));
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }

}