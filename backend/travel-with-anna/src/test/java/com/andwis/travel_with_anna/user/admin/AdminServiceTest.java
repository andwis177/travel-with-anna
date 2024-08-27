package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.handler.exception.UserNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.user.avatar.AvatarRepository;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import com.andwis.travel_with_anna.utility.PageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.*;
import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@DisplayName("Admin Service tests")
class AdminServiceTest {
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AvatarService avatarService;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Avatar avatar;
    private Avatar avatar2;
    private Long avatarId;
    private Long avatar2Id;
    private User user;
    private Long userId;
    private User secondaryUser;
    private Long secondaryUserId;
    @Autowired
    private UserService userService;

    @BeforeEach
    void setUp() {
        Role userRole = new Role();
        userRole.setRoleName(getUserRole());
        userRole.setAuthority(getUserAuthority());
        Optional<Role> existingUserRole = roleRepository.findByRoleName(getUserRole());
        Role retrivedUserRole = existingUserRole.orElseGet(() -> roleRepository.save(userRole));

        Role adminRole = new Role();
        adminRole.setRoleName(getAdminRole());
        adminRole.setAuthority(getAdminAuthority());
        Optional<Role> existingAdminRole = roleRepository.findByRoleName(getAdminRole());
        Role retrivedAdminRole = existingAdminRole.orElseGet(() -> roleRepository.save(adminRole));

        avatar = Avatar.builder()
                .avatar(AvatarImg.DEFAULT.getImg())
                .build();
        avatarId = avatarService.saveAvatar(avatar).getAvatarId();

        avatar2 = Avatar.builder()
                .avatar(AvatarImg.DEFAULT.getImg())
                .build();
        avatar2Id = avatarService.saveAvatar(avatar2).getAvatarId();

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedAdminRole)
                .avatarId(avatarId)
                .build();
        user.setAccountLocked(false);
        user.setEnabled(true);
        userId = userRepository.save(user).getUserId();

        secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedUserRole)
                .avatarId(avatar2Id)
                .build();
        secondaryUser.setAccountLocked(false);
        secondaryUser.setEnabled(true);
        secondaryUserId = userRepository.save(secondaryUser).getUserId();
    }

    @AfterEach
    void cleanUp() {
        avatarRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testGetAllUsers() {
        // Getter
        PageResponse<UserAdminView> result = adminService.getAllUsers(0, 10, createAuthentication(user));

        // When & Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(secondaryUserId, result.getContent().get(0).getUserId());

        Map<Long, byte[]> avatars = adminService.getAvatars(List.of(avatar2Id));
        assertNotNull(avatars.get(avatar2Id));

    }
    @Test
    void testGetAvatar() {
        // Getter
        byte[] avatarBytes = hexToBytes(avatar.getAvatar());

        // When
        UserAvatar userAvatar = adminService.getAvatar(userId);

        // Then
        assertNotNull(userAvatar);
        assertArrayEquals(avatarBytes, userAvatar.getAvatar());
    }


    @Test
    void testGetAvatars() {
        // Getter
        List<Long> avatarsId = List.of(avatarId, avatar2Id);
        byte[] avatarBytes = hexToBytes(avatar.getAvatar());
        byte[] avatar2Bytes = hexToBytes(avatar2.getAvatar());

        // When
        Map<Long, byte[]> avatars = adminService.getAvatars(avatarsId);

        // Then
        assertNotNull(avatars);
        assertEquals(2, avatars.size());
        assertArrayEquals(avatarBytes, avatars.get(avatarId));
        assertArrayEquals(avatar2Bytes, avatars.get(avatar2Id));
    }

    @Test
    void testGetUserAdminViewByIdentifier_UserId() {
        // Getter

        // When
        UserAdminView userAdminView = adminService.getUserAdminViewByIdentifier(secondaryUserId.toString(), createAuthentication(user));

        // Then
        assertNotNull(userAdminView);
        assertEquals(secondaryUserId, userAdminView.getUserId());
    }

    @Test
    void testGetUserAdminViewByIdentifier_UserName() {
        // Getter

        // When
        UserAdminView userAdminView = adminService.getUserAdminViewByIdentifier(secondaryUser.getUserName(), createAuthentication(user));

        // Then
        assertNotNull(userAdminView);
        assertEquals(secondaryUserId, userAdminView.getUserId());
        assertEquals(secondaryUser.getUserName(), userAdminView.getUserName());
    }

    @Test
    void testUpdateUser() throws RoleNotFoundException {
        // Getter
        UserAdminEdit userAdminEdit = new UserAdminEdit(
                secondaryUserId, true, false, getAdminRole());

        UserAdminUpdateRequest request = UserAdminUpdateRequest.builder()
                .userAdminEdit(userAdminEdit)
                .password("password")
                .build();

        // When
        adminService.updateUser(request, createAuthentication(user));

        // Then
        assertTrue(secondaryUser.isAccountLocked());
        assertFalse(secondaryUser.isEnabled());
        assertEquals(getAdminRole(), secondaryUser.getRole().getRoleName());
    }

    @Test
    void getUserAdminViewByIdentifier_Email() {
        // Getter

        // When
        UserAdminView userAdminView = adminService.getUserAdminViewByIdentifier(user.getEmail(), createAuthentication(secondaryUser));

        // Then
        assertNotNull(userAdminView);
        assertEquals(userId, userAdminView.getUserId());
        assertEquals(user.getEmail(), userAdminView.getEmail());
    }

    @Test
    void getUserAdminViewByIdentifier_OwnIdentifier() {
        // Getter
        // When & Then
        assertThrows(UserNotFoundException.class,
                () -> adminService.getUserAdminViewByIdentifier(user.getEmail(), createAuthentication(user)));
    }

    @Test
    void testDeleteUser() {
        // Getter
        UserAdminDeleteRequest request = new UserAdminDeleteRequest(secondaryUserId, "password");
        // When
        adminService.deleteUser(request, createAuthentication(user));

        // Then
        assertFalse(userService.existsByUserId(secondaryUserId));
    }

    @Test
    void testGetAllRoleNamesWithAdmin() {
        // Getter
        List<String> roles = adminService.getAllRoleNamesWithAdmin();

        // When & Then
        assertNotNull(roles);
        assertTrue(roles.contains(getUserRole()));
        assertTrue(roles.contains(getAdminRole()));
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}