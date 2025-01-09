package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.user.UserAvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AvatarFacade {

    private final UserAvatarService userAvatarService;

    public void setAvatar(MultipartFile file, UserDetails connectedUser)
            throws IOException {
        userAvatarService.setAvatar(file, connectedUser);
    }

    public byte[] getAvatar(UserDetails connectedUser) {
        return userAvatarService.getAvatar(connectedUser);
    }
}
