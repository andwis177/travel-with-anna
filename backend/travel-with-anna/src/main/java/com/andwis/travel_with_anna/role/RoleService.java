package com.andwis.travel_with_anna.role;

import com.andwis.travel_with_anna.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final UserService userService;

    private static final String ADMIN_ROLE_NAME = RoleType.ADMIN.getRoleName();

    @Transactional(readOnly = true)
    public List<String> getAllRoleNames() {
        List<String> roleNames = roleRepository.findAll().stream()
                .map(Role::getRoleName)
                .sorted()
                .collect(Collectors.toList());
        if (userService.existsByRoleName(RoleType.ADMIN.getRoleName())) {
            roleNames.remove(RoleType.ADMIN.getRoleName());
        }

        return filterAdminRole(roleNames);
    }

    @Transactional(readOnly = true)
    public Role getRoleByName(String roleName) throws RoleNotFoundException {
        return roleRepository.findByRoleName(roleName).orElseThrow(() ->
                new RoleNotFoundException(String.format("Role with name '%s' not found", roleName)));
    }

    private List<String> filterAdminRole(List<String> roleNames) {
        if (userService.existsByRoleName(ADMIN_ROLE_NAME)) {
            roleNames.remove(ADMIN_ROLE_NAME);
        }
        return roleNames;
    }
}
