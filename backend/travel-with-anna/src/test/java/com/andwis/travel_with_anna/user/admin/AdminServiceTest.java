package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.handler.exception.UserNotFoundException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.*;
import org.jetbrains.annotations.NotNull;
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
import java.util.HashSet;
import java.util.List;
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
                .avatar(AvatarDefaultImg.DEFAULT.getImg())
                .build();
        Long avatarId = avatarService.saveAvatar(avatar).getAvatarId();

        Avatar avatar2 = Avatar.builder()
                .avatar(AvatarDefaultImg.DEFAULT.getImg())
                .build();
        Long avatar2Id = avatarService.saveAvatar(avatar2).getAvatarId();

        Trip trip = Trip.builder()
                .tripName("tripName")
                .build();

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
                .ownedTrips(new HashSet<>())
                .build();
        secondaryUser.addTrip(trip);
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
    void testGetAvatar() {
        // Getter
        byte[] avatarBytes = hexToBytes(avatar.getAvatar());

        // When
        AvatarImg userAvatar = adminService.getAvatar(userId);

        // Then
        assertNotNull(userAvatar);
        assertArrayEquals(avatarBytes, userAvatar.avatar());
    }

    @Test
    void testGetUserAdminViewByIdentifier_UserId() {
        // Getter
        // When
        UserAdminResponse userAdminView = adminService.getUserAdminViewByIdentifier(secondaryUserId.toString(), createAuthentication(user));

        // Then
        assertNotNull(userAdminView);
        assertEquals(secondaryUserId, userAdminView.userId());
    }

    @Test
    void testGetUserAdminViewByIdentifier_UserName() {
        // Getter
        // When
        UserAdminResponse userAdminView = adminService.getUserAdminViewByIdentifier(secondaryUser.getUserName(), createAuthentication(user));

        // Then
        assertNotNull(userAdminView);
        assertEquals(secondaryUserId, userAdminView.userId());
        assertEquals(secondaryUser.getUserName(), userAdminView.userName());
    }

    @Test
    void testUpdateUser() throws RoleNotFoundException, WrongPasswordException {
        // Getter
        UserAdminEditRequest userAdminEdit = new UserAdminEditRequest(
                secondaryUserId, true, false, getAdminRole());

        UserAdminUpdateRequest request = UserAdminUpdateRequest.builder()
                .userAdminEditRequest(userAdminEdit)
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
        UserAdminResponse userAdminView = adminService.getUserAdminViewByIdentifier(user.getEmail(), createAuthentication(secondaryUser));

        // Then
        assertNotNull(userAdminView);
        assertEquals(userId, userAdminView.userId());
        assertEquals(user.getEmail(), userAdminView.email());
    }

    @Test
    void getUserAdminViewByIdentifier_OwnIdentifier() {
        // Getter
        // When & Then
        assertThrows(UserNotFoundException.class,
                () -> adminService.getUserAdminViewByIdentifier(user.getEmail(), createAuthentication(user)));
    }

    @Test
    void testDeleteUser() throws WrongPasswordException {
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

    private @NotNull Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}