package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.user.admin.UserAdminResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Map;

@Service
public class UserMapper {

    public UserAdminResponse toUserForAdminView(User user, Map<Long, byte[]> avatarsWithUsersId) {
        LocalDate lastModifiedDate =
                user.getLastModifiedDate() != null
                        ? user.getLastModifiedDate().toLocalDate()
                        : null;

        return new UserAdminResponse(
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.isAccountLocked(),
                user.isEnabled(),
                user.getCreatedDate().toLocalDate(),
                lastModifiedDate,
                user.getRole().getRoleName(),
                avatarsWithUsersId.get(user.getAvatarId())
        );
    }
}
