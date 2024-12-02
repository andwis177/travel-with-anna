package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.user.UserResponse;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminFacade {
    private final AdminService adminService;

    public PageResponse<UserAdminResponse> getAllUsers(int page, int size, UserDetails connectedUser) {
        return adminService.getAllUsers(page, size, connectedUser);
    }

    public UserAdminResponse getUserAdminViewByIdentifier(String identifier, UserDetails connectedUser) {
        return adminService.getUserAdminViewByIdentifier(identifier, connectedUser);
    }

    public AvatarImg getAvatar(Long userId) {
        return adminService.getAvatar(userId);
    }

    public void updateUser(UserAdminUpdateRequest request, UserDetails authentication)
            throws RoleNotFoundException, WrongPasswordException {
        adminService.updateUser(request, authentication);
    }

    public UserResponse deleteUser(UserAdminDeleteRequest request, UserDetails authentication)
            throws WrongPasswordException {
        return adminService.deleteUser(request, authentication);
    }

    public List<String> getAllRoleNamesWithAdmin() {
        return adminService.getAllRoleNamesWithAdmin();
    }
}
