package com.andwis.travel_with_anna.user.avatar;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("avatar")
@RequiredArgsConstructor
@Tag(name = "Avatar")
public class AvatarController {
    private final AvatarFacade facade;

    @PostMapping
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file")  MultipartFile file,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws IOException {
        facade.setAvatar(file, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<byte[]> getCurrentUserAvatar(
            @AuthenticationPrincipal UserDetails connectedUser) {
        byte[] avatarBytes = facade.getAvatar(connectedUser);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(avatarBytes.length);
        headers.setContentDispositionFormData("attachment", "avatar.jpg");

        return new ResponseEntity<>(avatarBytes, headers, HttpStatus.OK);
    }
}
