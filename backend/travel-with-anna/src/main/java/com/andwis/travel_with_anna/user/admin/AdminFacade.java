package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.user.UserResponse;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFacade {
    private final AdminService adminService;

    public PageResponse<UserAdminResponse> getAllUsers(int page, int size, Authentication connectedUser) {
        return adminService.getAllUsers(page, size, connectedUser);
    }

    public UserAdminResponse getUserAdminViewByIdentifier(String identifier, Authentication connectedUser) {
        return adminService.getUserAdminViewByIdentifier(identifier, connectedUser);
    }

    public AvatarImg getAvatar(Long userId) {
        return adminService.getAvatar(userId);
    }

    public void updateUser(UserAdminUpdateRequest request, Authentication authentication) throws RoleNotFoundException {
        adminService.updateUser(request, authentication);
    }

    public UserResponse deleteUser(UserAdminDeleteRequest request, Authentication authentication) {
        return adminService.deleteUser(request, authentication);
    }

    public List<String> getAllRoleNamesWithAdmin() {
        return adminService.getAllRoleNamesWithAdmin();
    }
}
