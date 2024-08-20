package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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

    private Long avatar2Id;
    private User user;
    private Long userId;

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

        Avatar avatar = Avatar.builder()
                .avatar(AvatarImg.DEFAULT.getImg())
                .build();
        Long avatarId = avatarService.save(avatar).getAvatarId();

        Avatar avatar2 = Avatar.builder()
                .avatar(AvatarImg.DEFAULT.getImg())
                .build();
        avatar2Id = avatarService.save(avatar2).getAvatarId();

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedAdminRole)
                .avatarId(avatarId)
                .build();
        user.setEnabled(true);
        userRepository.save(user);

        User secondaryUser = User.builder()
                .userName("userName2")
                .email("email2@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedUserRole)
                .avatarId(avatar2Id)
                .build();
        user.setEnabled(true);
        userId = userRepository.save(secondaryUser).getUserId();
    }

    @AfterEach
    void cleanUp() {
        avatarRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getAllUsers() {
        // Getter
        PageResponse<UserAdminView> result = adminService.getAllUsers(0, 10, createAuthentication(user));

        // When
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(userId, result.getContent().get(0).getUserId());

        // Then
        Map<Long, byte[]> avatars = adminService.getAvatars(List.of(avatar2Id));
        assertNotNull(avatars.get(avatar2Id));

    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}