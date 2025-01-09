package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.AvatarNotFoundException;
import com.andwis.travel_with_anna.user.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;

@Service
@RequiredArgsConstructor
public class AvatarService {

    private static final String DEFAULT_AVATAR_IMAGE = AvatarDefaultImg.DEFAULT.getImg();

    private final AvatarRepository avatarRepository;

    public Avatar saveAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    public boolean isAvatarExistsById(Long id) {
        return avatarRepository.existsById(id);
    }

    public Avatar findById(Long id) {
        return avatarRepository.findById(id)
                .orElseThrow(() -> new AvatarNotFoundException("Avatar not found"));
    }

    public Avatar createAvatar(@NotNull User user) {
        Avatar avatar = avatarRepository.save(Avatar.builder()
                .avatar(null)
                .build());
        user.setAvatarId(avatar.getAvatarId());
        return avatar;
    }

    @Transactional
    public void deleteAvatar(@NotNull User user) {
        if (user.getAvatarId() != null) {
            Long avatarId = user.getAvatarId();
            user.setAvatarId(null);
            avatarRepository.deleteById(avatarId);
        }
    }

    public AvatarImg getAvatar(@NotNull User user) {
        Avatar avatar = findById(user.getAvatarId());
        String avatarHex = resolveAvatarHex(avatar);
        return new AvatarImg(
                hexToBytes(avatarHex)
        );
    }

    public Map<Long, byte[]> getAvatars(@NotNull List<Long> avatarsId) {
        return avatarsId.stream()
                .filter(this::isAvatarExistsById)
                .collect(Collectors.toMap(
                        avatarId -> avatarId,
                        avatarId -> hexToBytes(resolveAvatarHex(findById(avatarId)))
                ));
    }

    private String resolveAvatarHex(Avatar avatar) {
        if (avatar != null && avatar.getAvatar() != null && !avatar.getAvatar().isEmpty()) {
            return avatar.getAvatar();
        }
        return DEFAULT_AVATAR_IMAGE;
    }
}