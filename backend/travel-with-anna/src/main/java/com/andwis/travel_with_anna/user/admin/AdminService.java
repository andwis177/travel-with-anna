package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.handler.exception.UserNotFoundException;
import com.andwis.travel_with_anna.role.RoleService;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserMapper;
import com.andwis.travel_with_anna.user.UserResponse;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import com.andwis.travel_with_anna.utility.NumberUtils;
import com.andwis.travel_with_anna.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Map;

import static com.andwis.travel_with_anna.role.Role.getAdminRole;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final AvatarService avatarService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final RoleService roleService;

    public PageResponse<UserAdminResponse> getAllUsers(int page, int size, Authentication connectedUser) {
        var user  = userService.getConnectedUser(connectedUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        Page<User> users = userService.getAllUsersExcept(pageable, user.getUserId());
        List<Long> avatarsId = users.map(User::getAvatarId).getContent();
        Map<Long, byte[]> avatarsWithUsersId = avatarService.getAvatars(avatarsId);
        List<UserAdminResponse> userAdminViewList = users.stream()
                .map(theUser -> userMapper.toUserForAdminView(theUser, avatarsWithUsersId)).toList();
        return new PageResponse<>(
                userAdminViewList,
                users.getNumber(),
                users.getSize(),
                users.getTotalElements(),
                users.getTotalPages(),
                users.isFirst(),
                users.isLast()
        );
    }

    public AvatarImg getAvatar(Long userId) {
        User user = userService.getUserById(userId);
        return avatarService.getAvatar(user);

    }

    public UserAdminResponse getUserAdminViewByIdentifier(String identifier, Authentication connectedUser) {
        User user = getUserBasedOnIdentifier(identifier);
        if (user.getUserId().equals(userService.getConnectedUser(connectedUser).getUserId())) {
            throw new UserNotFoundException("You can't view your own profile");
        }
        List<Long> avatarsId = List.of(user.getAvatarId());
        Map<Long, byte[]> avatarsWithUsersId = avatarService.getAvatars(avatarsId);
        return userMapper.toUserForAdminView(user, avatarsWithUsersId);
    }

    private User getUserBasedOnIdentifier(String identifier){
        if (NumberUtils.isLong(identifier)) {
            return userService.getUserById(Long.parseLong(identifier));
        } else {
            if (userService.existsByEmail(identifier)) {
                return userService.getUserByEmail(identifier);
            }
            if (userService.existsByUserName(identifier)) {
                return userService.getUserByUserName(identifier);
            }
        }
        throw new UserNotFoundException("User not found");
    }

    public Long updateUser(@NotNull UserAdminUpdateRequest request, Authentication authentication) throws RoleNotFoundException {
        User adminUser = userService.getConnectedUser(authentication);
        userService.verifyPassword(adminUser, request.getPassword());
        UserAdminEditRequest userAdminEditRequest = request.getUserAdminEditRequest();
        User user = userService.getUserById(userAdminEditRequest.userId());
        user.setAccountLocked(userAdminEditRequest.accountLocked());
        user.setEnabled(userAdminEditRequest.enabled());
        user.setRole(roleService.getRoleByName(userAdminEditRequest.roleName()));
        userService.saveUser(user);
        return user.getUserId();
    }

    public UserResponse deleteUser(@NotNull UserAdminDeleteRequest request, Authentication authentication) {
        User adminUser = userService.getConnectedUser(authentication);
        userService.verifyPassword(adminUser, request.password());
        userService.deleteUserById(request.userId());
        return UserResponse.builder().message("User has been deleted").build();
    }

    public List<String> getAllRoleNamesWithAdmin() {
        List<String> roles = roleService.getAllRoleNames();
        if (!roles.contains(getAdminRole())) {
            roles.add(getAdminRole());
        }
        return roles;
    }
}