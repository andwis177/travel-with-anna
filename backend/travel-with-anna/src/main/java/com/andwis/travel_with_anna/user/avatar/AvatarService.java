package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.AvatarNotFoundException;
import com.andwis.travel_with_anna.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarRepository avatarRepository;

    public Avatar save(Avatar avatar) {
        return avatarRepository.save(avatar);
    }

    public boolean existsById(Long id) {
        return avatarRepository.existsById(id);
    }

    public Avatar findById(Long id) {
        return avatarRepository.findById(id)
                .orElseThrow(() -> new AvatarNotFoundException("Avatar not found"));
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static byte[] hexToBytes(String hex) {
        if (hex == null || hex.length() % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex string");
        }

        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            bytes[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return bytes;
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
}