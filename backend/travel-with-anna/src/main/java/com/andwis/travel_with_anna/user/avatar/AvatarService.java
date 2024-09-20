package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.AvatarNotFoundException;
import com.andwis.travel_with_anna.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarRepository avatarRepository;

    public Avatar saveAvatar(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    public boolean existsById(Long id) {
        return avatarRepository.existsById(id);
    }

    public Avatar findById(Long id) {
        return avatarRepository.findById(id)
                .orElseThrow(() -> new AvatarNotFoundException("Avatar not found"));
    }

    public Avatar createAvatar(User user) {
        Avatar avatar = avatarRepository.save(Avatar.builder()
                .avatar(null)
                .build());
        user.setAvatarId(avatar.getAvatarId());
        return avatar;
    }

    public void deleteAvatar(User user) {
        if (user.getAvatarId() != null) {
            avatarRepository.deleteById(user.getAvatarId());
            user.setAvatarId(null);
        }
    }

    public AvatarImg getAvatar(User user) {
        Avatar avatar = findById(user.getAvatarId());
        String avatarHex = (
                avatar != null &&
                        avatar.getAvatar() != null &&
                        !avatar.getAvatar().isEmpty()
        )
                ? avatar.getAvatar()
                : AvatarDefaultImg.DEFAULT.getImg();
        return new AvatarImg(
                hexToBytes(avatarHex)
        );
    }

    public Map<Long, byte[]> getAvatars(List<Long> avatarsId) {
        return avatarsId.stream()
                .filter(this::existsById)
                .collect(Collectors.toMap(
                        avatarId -> avatarId,
                        avatarId -> {
                            Avatar avatar = findById(avatarId);
                            String avatarHex = (
                                    avatar != null &&
                                            avatar.getAvatar() != null &&
                                            !avatar.getAvatar().isEmpty()
                            )
                                    ? avatar.getAvatar()
                                    : AvatarDefaultImg.DEFAULT.getImg();
                            return hexToBytes(avatarHex);
                        }
                ));
    }
}