package com.andwis.travel_with_anna.role;

import com.andwis.travel_with_anna.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static com.andwis.travel_with_anna.role.Role.getAdminRole;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final UserService userService;

    public List<String> getAllRoleNames() {
        List<String> roles = roleRepository.findAll().stream()
                .map(Role::getRoleName).sorted().collect(Collectors.toList());
        if (userService.existsByRoleName(getAdminRole())) {
            roles.remove(getAdminRole());
        }
        return roles;
    }

    public Role getRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByRoleName(roleName).orElseThrow(() ->
                new RoleNotFoundException("Role not found"));
    }
}
