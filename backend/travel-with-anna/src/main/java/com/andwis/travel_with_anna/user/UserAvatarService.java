package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.FileNotSavedException;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarDefaultImg;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.andwis.travel_with_anna.utility.ByteConverter.bytesToHex;
import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;

@Service
@RequiredArgsConstructor
public class UserAvatarService {

    private static final String JPEG_CONTENT_TYPE = "image/jpeg";
    private static final String JPG_CONTENT_TYPE = "image/jpg";
    private static final int MAX_FILE_SIZE = 1024 * 1024; // 1MB

    private final UserService userService;
    private final UserAuthenticationService userAuthenticationService;
    private final AvatarService avatarService;

    public void setAvatar(@NotNull MultipartFile file, UserDetails connectedUser)
            throws IOException {
        String contentType = file.getContentType();

        validateFileType(contentType);

        byte[] fileBytes = file.getBytes();
        User user = userAuthenticationService.retriveConnectedUser(connectedUser);

        if (fileBytes.length > MAX_FILE_SIZE) {
            throw new FileNotSavedException("File is too big");
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

    private void validateFileType(String contentType) throws FileNotSavedException {
        if (!JPEG_CONTENT_TYPE.equals(contentType) && !JPG_CONTENT_TYPE.equals(contentType)) {
            throw new FileNotSavedException("File is not a JPEG image. Actual type: " + contentType);
        }
    }

    public byte[] getAvatar(UserDetails connectedUser) {
        User user = userAuthenticationService.retriveConnectedUser(connectedUser);

        if (user.getAvatarId() != null) {
            if (avatarService.isAvatarExistsById(user.getAvatarId())) {
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
