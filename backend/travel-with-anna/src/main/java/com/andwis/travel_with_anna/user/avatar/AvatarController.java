package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.SaveAvatarException;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("avatar")
@RequiredArgsConstructor
@Tag(name = "Avatar")
public class AvatarController {
    private final static Path path = Paths.get("src/main/resources/static/default_avatar.jpg");
    private final AvatarService service;

    @PostMapping("/upload-avatar")
    public ResponseEntity<?> uploadAvatar(@RequestParam("file")  MultipartFile file, Authentication connectedUser) throws IOException {
        service.setAvatar(file, connectedUser);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/get-avatar")
    public ResponseEntity<byte[]> getCurrentUserAvatar(Authentication connectedUser) throws SaveAvatarException {
        byte[] avatarBytes = service.getAvatar(connectedUser, path);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        headers.setContentLength(avatarBytes.length);
        headers.setContentDispositionFormData("attachment", "avatar.jpg");

        return new ResponseEntity<>(avatarBytes, headers, HttpStatus.OK);
    }
}
