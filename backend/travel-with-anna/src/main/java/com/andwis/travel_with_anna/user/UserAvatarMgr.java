package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.FileNotSaved;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarDefaultImg;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.andwis.travel_with_anna.utility.ByteConverter.bytesToHex;
import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;

@Service
@RequiredArgsConstructor
public class UserAvatarMgr {
    private final UserService userService;
    private final AvatarService avatarService;


    public void setAvatar(@NotNull MultipartFile file, Authentication connectedUser)
            throws IOException {
        String contentType = file.getContentType();

        if (!"image/jpeg".equals(contentType) && !"image/jpg".equals(contentType)) {
            throw new FileNotSaved("File is not a JPEG image. Actual type: " + contentType);
        }

        byte[] fileBytes = file.getBytes();
        User user = userService.getConnectedUser(connectedUser);

        if (fileBytes.length > 1024 * 1024) {
            throw new FileNotSaved("File is too big");
        }

        if (user.getAvatarId() == null) {
            Avatar newAvatar = avatarService.createAvatar(user);
            user.setAvatarId(newAvatar.getAvatarId());
            userService.saveUser(user);
        }

        Avatar userAvatar = avatarService.findById(user.getAvatarId());
        userAvatar.setAvatar(bytesToHex(fileBytes));
        avatarService.saveAvatar(userAvatar);
    }

    public byte[] getAvatar(Authentication connectedUser) {
        User user = userService.getConnectedUser(connectedUser);

        if (user.getAvatarId() != null) {
            if (avatarService.existsById(user.getAvatarId())) {
                Avatar avatar = avatarService.findById(user.getAvatarId());
                if (avatar != null && avatar.getAvatar() != null) {
                    if (!avatar.getAvatar().isEmpty()) {
                        return hexToBytes(avatar.getAvatar());
                    }
                }
            }
        }
        return hexToBytes(AvatarDefaultImg.DEFAULT.getImg());
    }
}
