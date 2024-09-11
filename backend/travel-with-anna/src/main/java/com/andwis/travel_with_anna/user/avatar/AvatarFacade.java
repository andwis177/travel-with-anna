package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.user.UserAvatarMgr;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AvatarFacade {
    private final UserAvatarMgr userAvatarMgr;

    public void setAvatar(MultipartFile file, Authentication connectedUser)
            throws IOException {
        userAvatarMgr.setAvatar(file, connectedUser);
    }

    public byte[] getAvatar(Authentication connectedUser) {
        return userAvatarMgr.getAvatar(connectedUser);
    }

}
