package com.andwis.travel_with_anna.role;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Role Repository tests")
class RoleRepositoryTest {

    @Autowired
    private RoleRepository roleRepository;

    @Test
    void testFindByRoleName() {
        //Given
        Role role = Role.builder()
                .roleName("TEST_ROLE")
                .build();
        Integer roleId = roleRepository.save(role).getRoleId();

        //When
        Role retrivedRole = roleRepository.findByRoleName("TEST_ROLE").orElse(null);

        //Then
        assertNotNull(retrivedRole);
        assertEquals(roleId, retrivedRole.getRoleId());
        assertEquals("TEST_ROLE", role.getRoleName());
    }

    @Test
    void testFindByRoleName_Failure() {
        //Given
        //When
        Role retrivedRole = roleRepository.findByRoleName("ROLE_NON_EXISTING").orElse(null);

        //Then
        assertNull(retrivedRole);
    }
}