package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.user.User;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserMapper {

    public UserAdminView toUserForAdminView(User user, Map<Long, byte[]> avatarsWithUsersId) {

        return UserAdminView.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .accountLocked(user.isAccountLocked())
                .enabled(user.isEnabled())
                .createdDate(user.getCreatedDate().toLocalDate())
                .lastModifiedDate(user.getLastModifiedDate().toLocalDate())
                .roleName(user.getRole().getRoleName())
                .cover(avatarsWithUsersId.get(user.getAvatarId()))
                .build();
    }
}
