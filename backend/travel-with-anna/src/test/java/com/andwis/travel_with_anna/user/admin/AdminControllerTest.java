package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.UserRespond;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
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
import static com.andwis.travel_with_anna.user.avatar.AvatarService.hexToBytes;
import static org.apache.commons.lang3.Conversion.hexToByte;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

    @Autowired
    private ObjectMapper objectMapper;

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
        userRepository.save(user);

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
        user.setAccountLocked(false);
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
    void shouldGetAllUsers() throws Exception {
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
                .avatar(null)
                .build();

        PageResponse<UserAdminView> response = new PageResponse<>(
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
        UserAdminView userResponse = UserAdminView.builder()
                .userId(1L)
                .userName("TestUser")
                .email("email@example.com")
                .accountLocked(false)
                .enabled(true)
                .createdDate(LocalDate.of(2024, 8, 19))
                .lastModifiedDate(LocalDate.of(2024, 9, 19))
                .roleName(getUserRole())
                .avatar(new byte[0])
                .build();

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

        byte[] avatarBytes = hexToBytes(AvatarImg.DEFAULT.getImg());
        UserAvatar userAvatar = UserAvatar.builder()
                .avatar(avatarBytes)
                .build();
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
        UserAdminEdit userAdminEdit = new UserAdminEdit(
                1L ,true, false, getUserRole());


        UserAdminUpdateRequest request = UserAdminUpdateRequest.builder()
                .password("adminPassword")
                .userAdminEdit(userAdminEdit)
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

        String jsonContent = objectMapper.writeValueAsString(request);
        String updatedJsonContent = objectMapper.writeValueAsString(updatedUser);

        when(adminService.updateUser(any(UserAdminUpdateRequest.class), any(Authentication.class)))
                .thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(patch("/admin/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().json(updatedJsonContent));
    }

    @Test
    @WithMockUser(username = "email@example.com", authorities = "Admin")
    void shouldDeleteUser() throws Exception {
        // Given
        Long userId = 1L;
        UserRespond userRespond = UserRespond.builder()
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