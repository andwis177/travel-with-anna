package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.AvatarNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.admin.AdminService;
import com.andwis.travel_with_anna.user.admin.UserAdminResponse;
import com.andwis.travel_with_anna.utility.PageResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static com.andwis.travel_with_anna.role.Role.*;
import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Avatar Service tests")
class AvatarServiceTest {
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private AvatarService avatarService;
    @Autowired
    private AdminService adminService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private Avatar avatar;
    private Avatar avatar2;
    private Long avatarId;
    private Long avatar2Id;
    private User user;
    private Long secondaryUserId;

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
        avatarId = avatarService.saveAvatar(avatar).getAvatarId();

        avatar2 = Avatar.builder()
                .avatar(AvatarDefaultImg.DEFAULT.getImg())
                .build();
        avatar2Id = avatarService.saveAvatar(avatar2).getAvatarId();

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedAdminRole)
                .avatarId(avatarId)
                .ownedTrips(new HashSet<>())
                .build();
        user.setAccountLocked(false);
        user.setEnabled(true);

        userRepository.save(user);

        User secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedUserRole)
                .avatarId(avatar2Id)
                .ownedTrips(new HashSet<>())
                .build();
        secondaryUser.setAccountLocked(false);
        secondaryUser.setEnabled(true);
        userRepository.save(secondaryUser);

        secondaryUserId = userRepository.save(secondaryUser).getUserId();
    }

    @AfterEach
    void cleanUp() {
        avatarRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }


    @Test
    void save_ShouldReturnSavedAvatar() {
        // Given
        Avatar avatar = new Avatar();

        // When
        Avatar savedAvatar = avatarService.saveAvatar(avatar);

        // Then
        assertNotNull(savedAvatar);
        assertEquals(avatar, savedAvatar);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenAvatarExists() {
        // Given
        Avatar avatar = new Avatar();
        Avatar savedAvatar = avatarService.saveAvatar(avatar);

        // When
        boolean result = avatarService.existsById(savedAvatar.getAvatarId());

        // Then
        assertTrue(result);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenAvatarDoesNotExist() {
        // Given
        // When
        boolean result = avatarService.existsById(999L);
        // Then
        assertFalse(result);
    }

    @Test
    void findById_ShouldReturnAvatar_WhenAvatarExists() {
        // Given
        Avatar avatar = new Avatar();
        Avatar savedAvatar = avatarService.saveAvatar(avatar);

        // When
        Avatar result = avatarService.findById(savedAvatar.getAvatarId());

        // Then
        assertNotNull(result);
        assertEquals(savedAvatar, result);
    }

    @Test
    void findById_ShouldThrowException_WhenAvatarDoesNotExist() {
        // Given
        // When & Then
        assertThrows(AvatarNotFoundException.class, () -> avatarService.findById(999L));
    }

    @Test
    void createAvatar_ShouldReturnCreatedAvatar() {
        // Given
        User user = new User();

        // When
        Avatar avatar = avatarService.createAvatar(user);

        // Then
        assertNotNull(avatar);
        assertEquals(avatar.getAvatarId(), user.getAvatarId());
    }

    @Test
    void deleteAvatar_ShouldDeleteAvatar_WhenAvatarIdExists() {
        // Given
        User user = new User();
        Avatar avatar = avatarService.createAvatar(user);
        user.setAvatarId(avatar.getAvatarId());

        // When
        avatarService.deleteAvatar(user);

        // Then
        assertNull(user.getAvatarId());
    }

    @Test
    void deleteAvatar_ShouldNotDeleteAvatar_WhenAvatarIdDoesNotExist() {
        // Given
        User user = new User();

        // When
        avatarService.deleteAvatar(user);

        // Then
        assertNull(user.getAvatarId());
    }

    @Test
    void testGetAllUsers() {
        // Getter
        PageResponse<UserAdminResponse> result = adminService.getAllUsers(0, 10, createAuthentication(user));

        // When & Then
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(secondaryUserId, result.getContent().getFirst().userId());

        Map<Long, byte[]> avatars = avatarService.getAvatars(List.of(avatar2Id));
        assertNotNull(avatars.get(avatar2Id));
    }

    @Test
    void testGetAvatars() {
        // Getter
        List<Long> avatarsId = List.of(avatarId, avatar2Id);
        byte[] avatarBytes = hexToBytes(avatar.getAvatar());
        byte[] avatar2Bytes = hexToBytes(avatar2.getAvatar());

        // When
        Map<Long, byte[]> avatars = avatarService.getAvatars(avatarsId);

        // Then
        assertNotNull(avatars);
        assertEquals(2, avatars.size());
        assertArrayEquals(avatarBytes, avatars.get(avatarId));
        assertArrayEquals(avatar2Bytes, avatars.get(avatar2Id));
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}