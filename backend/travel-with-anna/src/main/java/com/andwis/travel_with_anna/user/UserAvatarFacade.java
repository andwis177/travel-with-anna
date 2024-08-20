package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.SaveAvatarException;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.andwis.travel_with_anna.user.avatar.AvatarService.bytesToHex;
import static com.andwis.travel_with_anna.user.avatar.AvatarService.hexToBytes;

@Service
@RequiredArgsConstructor
public class UserAvatarFacade {
    private final UserService userService;
    private final AvatarService avatarService;


    public void setAvatar(MultipartFile file, Authentication connectedUser)
            throws IOException {
        String contentType = file.getContentType();

        if (!"image/jpeg".equals(contentType) && !"image/jpg".equals(contentType)) {
            throw new SaveAvatarException("File is not a JPEG image. Actual type: " + contentType);
        }

        byte[] fileBytes = file.getBytes();
        User user = userService.getConnectedUser(connectedUser);

        if (fileBytes.length > 1024 * 1024) {
            throw new SaveAvatarException("File is too big");
        }
        String fileHex = bytesToHex(fileBytes);
        if (user.getAvatarId() == null) {
            avatarService.createAvatar(user);
            userService.saveUser(user);
        }

        Avatar userAvatar = avatarService.findById(user.getAvatarId());
        userAvatar.setAvatar(fileHex);
        avatarService.save(userAvatar);
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
        return hexToBytes(AvatarImg.DEFAULT.getImg());
    }
}
