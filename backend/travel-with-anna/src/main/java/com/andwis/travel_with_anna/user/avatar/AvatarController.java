package com.andwis.travel_with_anna.user.avatar;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
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

    private static final String CONTENT_DISPOSITION_ATTACHMENT = "attachment";
    private static final String AVATAR_FILENAME = "avatar.jpg";

    private final AvatarFacade facade;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void uploadAvatar(
            @RequestParam("file")  MultipartFile avatarFile,
            @AuthenticationPrincipal UserDetails connectedUser)
            throws IOException {
        facade.setAvatar(avatarFile, connectedUser);
    }

    @GetMapping(produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getCurrentUserAvatar(
            @AuthenticationPrincipal UserDetails connectedUser) {
        byte[] avatarBytes = facade.getAvatar(connectedUser);
        HttpHeaders headers = createAvatarHeaders(avatarBytes.length);
        return new ResponseEntity<>(avatarBytes, headers, HttpStatus.OK);
    }

    private @NotNull HttpHeaders createAvatarHeaders(long contentLength) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(contentLength);
        headers.setContentDispositionFormData(CONTENT_DISPOSITION_ATTACHMENT, AVATAR_FILENAME);
        return headers;
    }
}
