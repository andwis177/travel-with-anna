package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.handler.exception.UserNotFoundException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.RoleService;
import com.andwis.travel_with_anna.role.RoleType;
import com.andwis.travel_with_anna.user.*;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AdminService {

    private static final String SORT_PAGE_BY = "userId";
    private static final String CANNOT_VIEW_OWN_PROFILE = "You can't view your own profile";
    private static final String USER_NOT_FOUND_MESSAGE = "User not found";

    private final AvatarService avatarService;
    private final UserMapper userMapper;
    private final UserService userService;
    private final UserAuthenticationService userAuthenticationService;
    private final RoleService roleService;

    public PageResponse<UserAdminResponse> getAllUsers(int page, int size, UserDetails connectedUser) {
        var currentUser  = userAuthenticationService.retriveConnectedUser(connectedUser);
        Pageable pageable = PageRequest.of(page, size, Sort.by(SORT_PAGE_BY).ascending());

        Page<User> users = userService.getAllUsersExcept(pageable, currentUser.getUserId());
        Map<Long, byte[]> avatarsWithUsersId = getAvatarsByIds(users.map(User::getAvatarId).getContent());

        List<UserAdminResponse> userAdminViewList = users.stream()
                .map(user -> userMapper.toUserForAdminView(user, avatarsWithUsersId))
                .toList();

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

    public UserAdminResponse getUserByIdentifier(String identifier, UserDetails connectedUser) {
        User user = findUserByIdentifier(identifier);
        User currentUser = userAuthenticationService.retriveConnectedUser(connectedUser);

        if (user.getUserId().equals(currentUser.getUserId())) {
            throw new UserNotFoundException(CANNOT_VIEW_OWN_PROFILE);
        }

        Map<Long, byte[]> avatarsWithUsersId = getAvatarsByIds(List.of(user.getAvatarId()));
        return userMapper.toUserForAdminView(user, avatarsWithUsersId);
    }

    private Map<Long, byte[]> getAvatarsByIds(List<Long> avatarIds) {
        return avatarService.getAvatars(avatarIds);
    }

    private User findUserByIdentifier(String identifier){
        if (NumberUtils.isValidLong(identifier)) {
            return userService.getUserById(Long.parseLong(identifier));
        }

        if (userService.existsByEmail(identifier)) {
            return userService.getUserByEmail(identifier);
        }
        if (userService.existsByUserName(identifier)) {
            return userService.getUserByUserName(identifier);
        }

        throw new UserNotFoundException(USER_NOT_FOUND_MESSAGE);
    }

    private void adminAuthentication(UserDetails authentication, String password) {
        User adminUser = userAuthenticationService.retriveConnectedUser(authentication);
        userAuthenticationService.verifyPassword(adminUser, password);
    }

    @Transactional
    public Long updateUser(@NotNull UserAdminUpdateRequest request, UserDetails authentication)
            throws RoleNotFoundException, WrongPasswordException {
        adminAuthentication(authentication, request.getPassword());

        UserAdminEditRequest editRequest = request.getUserAdminEditRequest();
        User user = userService.getUserById(editRequest.userId());

        user.setAccountLocked(editRequest.accountLocked());
        user.setEnabled(editRequest.enabled());
        user.setRole(roleService.getRoleByName(editRequest.roleName()));
        userService.saveUser(user);

        return user.getUserId();
    }

    @Transactional
    public UserResponse deleteUser(@NotNull UserAdminDeleteRequest request, UserDetails authentication) {

        adminAuthentication(authentication, request.password());

        User userToDelete = userService.getUserById(request.userId());
        userService.deleteUser(userToDelete);

        return UserResponse.builder()
                .message("User has been deleted")
                .build();
    }

    public List<String> getAllRoleNamesWithAdmin() {
        List<String> roles = roleService.getAllRoleNames();
        if (!roles.contains(RoleType.ADMIN.name())) {
            roles.add(RoleType.ADMIN.name());
        }
        return roles;
    }
}