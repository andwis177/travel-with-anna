package com.andwis.travel_with_anna.user.admin;

import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import com.andwis.travel_with_anna.utility.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.andwis.travel_with_anna.user.avatar.AvatarService.hexToBytes;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final AvatarService avatarService;
    private final UserMapper userMapper;
    private final UserService userService;

    public PageResponse<UserAdminView> getAllUsers(int page, int size, Authentication connectedUser) {
        var user  = userService.getConnectedUser(connectedUser);

        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").ascending());
        Page<User> users = userRepository.findAllExcept(pageable, user.getUserId());
        List<Long> avatarsId = users.map(User::getAvatarId).getContent();
        Map<Long, byte[]> avatarsWithUsersId = getAvatars(avatarsId);
        List<UserAdminView> userAdminViewList = users.stream()
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

    public Map<Long, byte[]> getAvatars(List<Long> avatarsId) {
        return avatarsId.stream()
                .filter(avatarService::existsById)
                .collect(Collectors.toMap(
                        avatarId -> avatarId,
                        avatarId -> {
                            Avatar avatar = avatarService.findById(avatarId);
                            String avatarHex = (
                                            avatar != null &&
                                            avatar.getAvatar() != null &&
                                            !avatar.getAvatar().isEmpty()
                            )
                                    ? avatar.getAvatar()
                                    : AvatarImg.DEFAULT.getImg();
                            return hexToBytes(avatarHex);
                        }
                ));
    }
}
