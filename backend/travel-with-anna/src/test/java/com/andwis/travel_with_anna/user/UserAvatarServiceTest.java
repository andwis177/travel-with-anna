package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.FileNotSavedException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarDefaultImg;
import com.andwis.travel_with_anna.user.avatar.AvatarRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("User Avatar Facade tests")
class UserAvatarServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserAvatarService userAvatarService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AvatarRepository avatarRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        avatarRepository.deleteAll();

        Role userRole = roleRepository.findByRoleName(USER.getRoleName()).orElse(
                roleRepository.save(Role.builder()
                        .roleName(USER.getRoleName())
                        .roleAuthority(USER.getAuthority())
                        .build()));

        Avatar avatar = Avatar.builder()
                .avatar(AvatarDefaultImg.DEFAULT.getImg())
                .build();
        Long avatarId = avatarRepository.save(avatar).getAvatarId();

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(userRole)
                .avatarId(avatarId)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);
        userRepository.save(user);
    }

    @Test
    @Transactional
    void testSetAvatar_Success() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        // When
        userAvatarService.setAvatar(file, createUserDetails(user));

        // Then
        Optional<User> retrievedUser = userRepository.findById(user.getUserId());
        assertTrue(retrievedUser.isPresent());
        assertNotNull(retrievedUser.get().getAvatarId());
        assertNotEquals(0, retrievedUser.get().getAvatarId());
    }

    @Test
    @Transactional
    void testSetAvatar_FileIsNotJpeg() {
        // Given
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.png",
                "image/png",
                "some-image-content".getBytes()
        );

        // When & Then
        FileNotSavedException exception = assertThrows(FileNotSavedException.class, () ->
                userAvatarService.setAvatar(file, createUserDetails(user)));

        assertEquals("File is not a JPEG image. Actual type: image/png", exception.getMessage());
    }

    @Test
    @Transactional
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
        FileNotSavedException exception = assertThrows(FileNotSavedException.class, () ->
                userAvatarService.setAvatar(file, createUserDetails(user)));

        assertEquals("File is too big", exception.getMessage());
    }

    @Test
    @Transactional
    void testSetAvatar_UserWithoutAvatar() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );

        user.setAvatarId(null);

        // When
        userAvatarService.setAvatar(file, createUserDetails(user));

        // Then
        Optional<User> retrievedUser = userRepository.findById(user.getUserId());
        assertTrue(retrievedUser.isPresent());
        assertNotNull(retrievedUser.get().getAvatarId());
        assertNotEquals(0, retrievedUser.get().getAvatarId());

    }

    @Test
    @Transactional
    void testGetAvatar_Success() throws IOException {
        // Given
        MultipartFile file = new MockMultipartFile(
                "avatar",
                "avatar.jpg",
                "image/jpeg",
                "some-image-content".getBytes()
        );
        userAvatarService.setAvatar(file, createUserDetails(user));

        // When
        byte[] avatarBytes = userAvatarService.getAvatar(createUserDetails(user));

        // Then
        assertNotNull(avatarBytes);
        assertEquals("some-image-content", new String(avatarBytes));
    }

    @Test
    @Transactional
    void testGetAvatar_DefaultAvatar(){
        // Given
        byte[] defaultImg = hexToBytes(AvatarDefaultImg.DEFAULT.getImg());

        // When
        byte[] avatar = userAvatarService.getAvatar(createUserDetails(user));

        // Then
        assertArrayEquals(defaultImg, avatar);
        assertNotNull(user.getAvatarId());
    }


    @Test
    @Transactional
    void testGetAvatar_FileNotExists() {
        // Given
        byte[] defaultImg = hexToBytes(AvatarDefaultImg.DEFAULT.getImg());
        user.setAvatarId(null);

        // When
        byte[] avatar = userAvatarService.getAvatar(createUserDetails(user));

        // Then
        assertArrayEquals(defaultImg, avatar);
    }

    private @NotNull UserDetails createUserDetails(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        securityUser,
                        user.getPassword(),
                        securityUser.getAuthorities()
                )
        );
        return securityUser;
    }
}
