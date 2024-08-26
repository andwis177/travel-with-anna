package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.user.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserMapper {

    public UserAdminView toUserForAdminView(User user, Map<Long, byte[]> avatarsWithUsersId) {
        UserAdminView userAdminView = UserAdminView.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .accountLocked(user.isAccountLocked())
                .enabled(user.isEnabled())
                .createdDate(user.getCreatedDate().toLocalDate())
                .roleName(user.getRole().getRoleName())
                .avatar(avatarsWithUsersId.get(user.getAvatarId()))
                .build();
        if (user.getLastModifiedDate() != null) {
            userAdminView.setLastModifiedDate(user.getLastModifiedDate().toLocalDate());
        }

        return userAdminView;
    }
}
