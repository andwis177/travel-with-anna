package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import com.andwis.travel_with_anna.user.*;
import com.andwis.travel_with_anna.user.avatar.AvatarDefaultImg;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.utility.PageResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.*;
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
    private TripRepository tripRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @MockBean
    private AdminService adminService;

    private User user;
    private Role retrivedAdminRole;
    private User adminUser;

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
        user.setAccountLocked(false);
        user.setEnabled(true);

        Role adminRole = new Role();
        adminRole.setRoleName(getAdminRole());
        adminRole.setAuthority(getAdminAuthority());
        Optional<Role> existingAdminRole = roleRepository.findByRoleName(getAdminRole());
        retrivedAdminRole = existingAdminRole.orElseGet(() -> roleRepository.save(adminRole));

        adminUser = User.builder()
                .userName("adminUserName")
                .email("adminEmail@example.com")
                .password(passwordEncoder.encode("adminPassword"))
                .role(retrivedAdminRole)
                .avatarId(2L)

                .build();
        adminUser.setAccountLocked(false);
        adminUser.setEnabled(true);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        tripRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetAllUsers() throws Exception {
        // Getter
        UserAdminResponse userResponse = new UserAdminResponse(
                1L,
                "TestUser",
                "testuser@example.com",
                false,
                true,
                LocalDate.of(2024, 8, 19),
                LocalDate.of(2024, 8, 19),
                "USER",
                null
        );

        PageResponse<UserAdminResponse> response = new PageResponse<>(
                List.of(userResponse),
                0, 10, 1, 1, true, true
        );

        String jsonContent = objectMapper.writeValueAsString(response);

        when(adminService.getAllUsers(anyInt(), anyInt(), any(Authentication.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(get("/admin/users")
                        .param("page", "0")
                        .param("size", "10")
                        .principal(createAuthentication(user))) // Simulate an authenticated user
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));

    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetAllUsersWithDefaultParams() throws Exception {
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

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldGetUserAdminViewByIdentifier() throws Exception {
        // Given
        UserAdminResponse userResponse = new UserAdminResponse(
                1L,
                "TestUser",
                "email@example.com",
                false,
                true,
                LocalDate.of(2024, 8, 19),
                LocalDate.of(2024, 9, 19),
                getUserRole(),
                new byte[0]
        );

        String identifier = "TestUser";
        String jsonContent = objectMapper.writeValueAsString(userResponse);

        when(adminService.getUserAdminViewByIdentifier(eq(identifier), any(Authentication.class)))
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
                1L ,true, false, getUserRole());

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
                .role(retrivedAdminRole)
                .avatarId(user.getAvatarId())
                .build();

        userService.saveUser(updatedUser);

        String jsonContent = objectMapper.writeValueAsString(request);

        when(adminService.updateUser(any(UserAdminUpdateRequest.class), any(Authentication.class)))
                .thenReturn(updatedUser.getUserId());

        // When & Then
        mockMvc.perform(patch("/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk());
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
        mockMvc.perform(delete("/admin/delete/{userId}", userId)
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
        List<String> roleNames = List.of(getUserRole(), getAdminRole());
        String jsonContent = objectMapper.writeValueAsString(roleNames);

        when(adminService.getAllRoleNamesWithAdmin()).thenReturn(roleNames);

        // When & Then
        mockMvc.perform(get("/admin/roles"))
                .andExpect(status().isOk())
                .andExpect(content().json(jsonContent));
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}