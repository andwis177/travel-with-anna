package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.SaveAvatarException;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AvatarService {
    private final AvatarRepository avatarRepository;
    private final UserService userService;

    public void setAvatar(MultipartFile file, Authentication connectedUser)
            throws IOException {

        if (!Objects.equals(file.getContentType(), "image/jpeg")) {
            throw new SaveAvatarException("File is not a jpeg image");
        }

        byte[] fileBytes = file.getBytes();
        User user = userService.getConnectedUser(connectedUser);

        if (fileBytes.length > 1024 * 1024) {
            throw new SaveAvatarException("File is too big");
        }
        String fileHex = bytesToHex(fileBytes);

        if (user.getAvatar() == null) {
            Avatar avatar = createAvatar(user);
            user.setAvatar(avatar);
        }

        Avatar userAvatar = user.getAvatar();
        userAvatar.setAvatar(fileHex);

        avatarRepository.save(userAvatar);
        userService.saveUser(user);
    }

    public byte[] getAvatar(Authentication connectedUser, Path path) throws SaveAvatarException {
        User user = userService.getConnectedUser(connectedUser);
        if (user.getAvatar() != null) {
            String avatar = user.getAvatar().getAvatar();
            if (avatar != null && !avatar.isEmpty()) {
                return hexToBytes(avatar);
            }
        }
        return getDefaultAvatar(path);
    }

    private byte[] getDefaultAvatar(Path path) throws SaveAvatarException {
        try {
            return Files.readAllBytes(path);
        } catch (Exception exp) {
            throw new SaveAvatarException("Error reading default avatar", exp);
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private static byte[] hexToBytes(String hex) {
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
        return avatarRepository.save(Avatar.builder().user(user).avatar("").build());
    }
}