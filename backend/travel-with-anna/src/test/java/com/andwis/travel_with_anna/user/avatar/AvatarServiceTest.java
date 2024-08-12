package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.SaveAvatarException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Avatar Service tests")
class AvatarServiceTest {

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;
    private Path path;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName("USER");
        Optional<Role> existingRole = roleRepository.findByRoleName("USER");
        Role retrivedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .roles(new HashSet<>(List.of(retrivedRole)))
                .avatar(new Avatar())
                .build();
        user.setEnabled(true);
        userRepository.save(user);

        path = Paths.get("src/test/resources/images/user.jpg");

    }

    @AfterEach()
    void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testSetAvatar_Success() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        // When
        avatarService.setAvatar(file, createAuthentication(user));

        // Then
        Optional<User> retrievedUser = userRepository.findById(user.getUserId());
        assertTrue(retrievedUser.isPresent());
        assertNotNull(retrievedUser.get().getAvatar());
        assertFalse(retrievedUser.get().getAvatar().getAvatar().isEmpty());
    }

    @Test
    void testSetAvatar_FileIsNotJpeg() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.png",
                "image/png",
                "some-image-content".getBytes()
        );

        // When & Then
        SaveAvatarException exception = assertThrows(SaveAvatarException.class, () -> {
            avatarService.setAvatar(file, createAuthentication(user));
        });

        assertEquals("File is not a jpeg image", exception.getMessage());
    }

    @Test
    void testSetAvatar_FileTooBig() {
        // Given
        byte[] largeFileContent = new byte[1024 * 1024 + 1];
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                largeFileContent
        );

        // When & Then
        SaveAvatarException exception = assertThrows(SaveAvatarException.class, () -> {
            avatarService.setAvatar(file, createAuthentication(user));
        });

        assertEquals("File is too big", exception.getMessage());
    }

    @Test
    void testSetAvatar_UserWithoutAvatar() throws IOException {
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        user.setAvatar(null);

        // When
        avatarService.setAvatar(file, createAuthentication(user));

        // Then
        Optional<User> retrievedUser = userRepository.findById(user.getUserId());
        assertTrue(retrievedUser.isPresent());
        assertNotNull(retrievedUser.get().getAvatar());
        assertFalse(retrievedUser.get().getAvatar().getAvatar().isEmpty());

    }

    @Test
    void testGetAvatar_Success() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );
        avatarService.setAvatar(file, createAuthentication(user));

        // When
        byte[] avatarBytes = avatarService.getAvatar(createAuthentication(user), path);

        // Then
        assertNotNull(avatarBytes);
        assertEquals("some-image-content", new String(avatarBytes));
    }

    @Test
    void testGetAvatar_DefaultAvatar() throws IOException {
        // Given
        byte[] avatarBytes = avatarService.getAvatar(createAuthentication(user), path);

        // When & Then
        assertNotNull(avatarBytes);
        byte[] expectedBytes = Files.readAllBytes(path);
        assertArrayEquals(expectedBytes, avatarBytes);
    }

    @Test
    void testGetAvatar_FileNotExists() throws IOException {
        // Given
        path = Path.of("non-existing-path");
        // When
        String errorMessage = assertThrows(SaveAvatarException.class, () -> avatarService.getAvatar(createAuthentication(user), path)).getMessage();
        // Then
        assertEquals("Error reading default avatar", errorMessage);
        assertNull(user.getAvatar().getAvatar());
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}
