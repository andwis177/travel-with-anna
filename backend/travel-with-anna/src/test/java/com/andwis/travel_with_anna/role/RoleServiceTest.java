package com.andwis.travel_with_anna.role;

import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.ADMIN;
import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@DisplayName("Role Service tests")
class RoleServiceTest {
    @Autowired
    private RoleService roleService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(USER.getRoleName());
        role.setRoleAuthority(USER.getAuthority());
        roleRepository.save(role);

        Role role2 = new Role();
        role2.setRoleName(ADMIN.getRoleName());
        role2.setRoleAuthority(ADMIN.getAuthority());
        roleRepository.save(role2);

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password("password")
                .role(role2)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
    }

    @AfterEach()
    void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void getAllRoleNames() {
        // Given
        // When
        List<String> roles = roleService.getAllRoleNames();

        // Then
        assertNotNull(roles);
        assertEquals(2, roles.size());
        assertEquals(ADMIN.getRoleName(), roles.get(0));
        assertEquals(USER.getRoleName(), roles.get(1));
    }

    @Test
    void getAllRoleNames_AdminExists() {
        // Given
        userRepository.save(user);
        // When
        List<String> roles = roleService.getAllRoleNames();

        // Then
        assertNotNull(roles);
        assertEquals(1, roles.size());
        assertEquals(USER.getRoleName(), roles.getFirst());
    }

    @Test
    void getRoleByName() throws RoleNotFoundException {
        // Given
        String roleName = USER.getRoleName();
        // When
        Role role = roleService.getRoleByName(roleName);

        // Then
        assertNotNull(role);
        assertEquals(USER.getRoleName(), role.getRoleName());
        assertEquals(USER.getAuthority(), role.getRoleAuthority());
    }

    @Test
    void getRoleByName_RoleNameNotExists() {
        // Given
        String roleName = "Role Name Not Exists";
        // When & Then
        assertThrows(RoleNotFoundException.class, () -> roleService.getRoleByName(roleName));
    }
}