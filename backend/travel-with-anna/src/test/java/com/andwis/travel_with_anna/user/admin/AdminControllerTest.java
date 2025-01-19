package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.UserResponse;
import com.andwis.travel_with_anna.user.avatar.AvatarDefaultImg;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.utility.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.ADMIN;
import static com.andwis.travel_with_anna.role.RoleType.USER;
import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Admin Controller tests")
class AdminControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AdminFacade adminFacade;

    private User user;
    private User adminUser;
    private Role adminRole;
    private UserDetails userDetails;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AdminService adminService() {
            return Mockito.mock(AdminService.class);
        }
    }

    @Autowired
    private AdminService adminService;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role userRole = Role.builder()
                .roleName(USER.getRoleName())
                .roleAuthority(USER.getAuthority())
                .build();
        roleRepository.save(userRole);

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .avatarId(1L)
                .build();
        user.setAccountLocked(false);
        user.setEnabled(true);

        adminRole = Role.builder()
                .roleName(ADMIN.getRoleName())
                .roleAuthority(ADMIN.getAuthority())
                .build();
        roleRepository.save(adminRole);

        adminUser = User.builder()
                .userName("adminUserName")
                .email("adminEmail@example.com")
                .password(passwordEncoder.encode("adminPassword"))
                .role(adminRole)
                .avatarId(2L)

                .build();
        adminUser.setAccountLocked(false);
        adminUser.setEnabled(true);

        userDetails = createUserDetails(adminUser);
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetAllUsers() throws Exception {
        // Getter
        UserAdminResponse userResponse = new UserAdminResponse(
                1L,
                "TestUser",
                "testuser@example.com",
                new byte[1],
                false,
                true,
                LocalDate.of(2024, 8, 19),
                LocalDate.of(2024, 8, 19),
                "USER"
        );

        PageResponse<UserAdminResponse> response = new PageResponse<>(
                List.of(userResponse),
                0, 10, 1, 1, true, true
        );

        when(adminService.getAllUsers(anyInt(), anyInt(), any()))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(createAuthentication(user)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(response)));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetAllUsersWithDefaultParams() throws Exception {
        // Given
        when(adminService.getAllUsers(anyInt(), anyInt(), any()))
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

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetUserAdminViewByIdentifier() throws Exception {
        // Given
        UserAdminResponse userResponse = new UserAdminResponse(
                1L,
                "TestUser",
                "email@example.com",
                new byte[1],
                false,
                true,
                LocalDate.of(2024, 8, 19),
                LocalDate.of(2024, 9, 19),
                USER.getRoleName()
        );

        String identifier = "TestUser";
        String jsonContent = objectMapper.writeValueAsString(userResponse);

        when(adminService.getUserByIdentifier(eq(identifier), any()))
                .thenReturn(userResponse);

        // When & Then
        mockMvc.perform(get("/admin/user/{identifier}", identifier)
                        .principal(createAuthentication(user)))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetAvatar() throws Exception {
        // Given
        byte[] avatarBytes = hexToBytes(AvatarDefaultImg.DEFAULT.getImg());
        AvatarImg userAvatar = new AvatarImg(
                avatarBytes
        );
        String jsonContent = objectMapper.writeValueAsString(userAvatar);
        when(adminService.getAvatar(any(Long.class))).thenReturn(userAvatar);

        // When & Then
        mockMvc.perform(get("/admin/avatar/{userId}", 1L)
                        .principal(createAuthentication(adminUser)))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    @Test
    @WithMockUser(username = "adminEmail@example.com", authorities = "Admin")
    void shouldUpdateUser() throws Exception {
        // Given
        UserAdminEditRequest userAdminEdit = new UserAdminEditRequest(
                1L ,true, false, USER.getRoleName());

        UserAdminUpdateRequest request = UserAdminUpdateRequest.builder()
                .password("adminPassword")
                .userAdminEditRequest(userAdminEdit)
                .build();

        User updatedUser = User.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .password(passwordEncoder.encode("password"))
                .accountLocked(true)
                .enabled(false)
                .role(adminRole)
                .avatarId(user.getAvatarId())
                .build();

        adminFacade.updateUser(request, userDetails);

        String jsonContent = objectMapper.writeValueAsString(request);

        when(adminService.updateUser(any(UserAdminUpdateRequest.class), any()))
                .thenReturn(updatedUser.getUserId());

        // When & Then
        mockMvc.perform(patch("/admin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isAccepted());
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldDeleteUser() throws Exception {
        // Given
        Long userId = 1L;
        UserResponse userRespond = UserResponse.builder()
                .message("User deleted successfully")
                .build();
        UserAdminDeleteRequest request = new UserAdminDeleteRequest(userId, "password");
        String jsonContent = objectMapper.writeValueAsString(request);

        when(adminService.deleteUser(eq(request), any())).thenReturn(userRespond);

        // When & Then
        mockMvc.perform(delete("/admin/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isNoContent())
                .andExpect(content().json("""
                    {
                        "message": "User deleted successfully"
                    }
                    """));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetAllRoleNamesWithAdmin() throws Exception {
        // Given
        List<String> roleNames = List.of(USER.getRoleName(), USER.getAuthority());
        String jsonContent = objectMapper.writeValueAsString(roleNames);

        when(adminService.getAllRoleNamesWithAdmin()).thenReturn(roleNames);

        // When & Then
        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
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

    private @NotNull Authentication createAuthentication(User user) {
        return new UsernamePasswordAuthenticationToken(
                userDetails,
                user.getPassword(),
                userDetails.getAuthorities()
        );
    }
}