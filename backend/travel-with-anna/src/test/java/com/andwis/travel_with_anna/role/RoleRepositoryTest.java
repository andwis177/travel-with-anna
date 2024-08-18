package com.andwis.travel_with_anna.role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Role Repository tests")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByRoleName() {
        // Given
        Role role = Role.builder()
                .roleName(getUserRole())
                .authority(getUserAuthority())
                .build();
        if (roleRepository.findByRoleName(getUserRole()).isPresent()) {
            roleRepository.delete(roleRepository.findByRoleName(getUserRole()).get());
        }
        Integer roleId = roleRepository.save(role).getRoleId();

        // When
        Role retrivedRole = roleRepository.findByRoleName(getUserRole()).orElse(null);

        // Then
        assertNotNull(retrivedRole);
        assertEquals(roleId, retrivedRole.getRoleId());
        assertEquals(getUserRole(), role.getRoleName());
        assertEquals(getUserAuthority(), role.getAuthority());
    }

    @Test
    void testFindByRoleName_Failure() {
        // Given
        // When
        Role retrivedRole = roleRepository.findByRoleName("ROLE_NON_EXISTING").orElse(null);

        // Then
        assertNull(retrivedRole);
    }
}