package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.FileNotSaved;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.avatar.Avatar;
import com.andwis.travel_with_anna.user.avatar.AvatarDefaultImg;
import com.andwis.travel_with_anna.user.avatar.AvatarRepository;
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
import java.util.HashSet;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static com.andwis.travel_with_anna.utility.ByteConverter.hexToBytes;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("User Avatar Facade tests")
class UserAvatarFacadeTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAvatarMgr userAvatarMgr;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User user;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrivedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        Avatar avatar = Avatar.builder()
                .avatar(AvatarDefaultImg.DEFAULT.getImg())
                .build();
        Long avatarId = avatarRepository.save(avatar).getAvatarId();

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(avatarId)
                .ownedTrips(new HashSet<>())
                .build();
        user.setEnabled(true);
        userRepository.save(user);
    }

    @AfterEach()
    void cleanUp() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
        avatarRepository.deleteAll();
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
        userAvatarMgr.setAvatar(file, createAuthentication(user));

        // Then
        Optional<User> retrievedUser = userRepository.findById(user.getUserId());
        assertTrue(retrievedUser.isPresent());
        assertNotNull(retrievedUser.get().getAvatarId());
        assertNotEquals(0, retrievedUser.get().getAvatarId());
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
        FileNotSaved exception = assertThrows(FileNotSaved.class, () -> {
            userAvatarMgr.setAvatar(file, createAuthentication(user));
        });

        assertEquals("File is not a JPEG image. Actual type: image/png", exception.getMessage());
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
        FileNotSaved exception = assertThrows(FileNotSaved.class, () -> {
            userAvatarMgr.setAvatar(file, createAuthentication(user));
        });

        assertEquals("File is too big", exception.getMessage());
    }

    @Test
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
        userAvatarMgr.setAvatar(file, createAuthentication(user));

        // Then
        Optional<User> retrievedUser = userRepository.findById(user.getUserId());
        assertTrue(retrievedUser.isPresent());
        assertNotNull(retrievedUser.get().getAvatarId());
        assertNotEquals(0, retrievedUser.get().getAvatarId());

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
        userAvatarMgr.setAvatar(file, createAuthentication(user));

        // When
        byte[] avatarBytes = userAvatarMgr.getAvatar(createAuthentication(user));

        // Then
        assertNotNull(avatarBytes);
        assertEquals("some-image-content", new String(avatarBytes));
    }

    @Test
    void testGetAvatar_DefaultAvatar(){
        // Given
        byte[] defaultImg = hexToBytes(AvatarDefaultImg.DEFAULT.getImg());

        // When
        byte[] avatar = userAvatarMgr.getAvatar(createAuthentication(user));

        // Then
        assertArrayEquals(defaultImg, avatar);
        assertNotNull(user.getAvatarId());
    }


    @Test
    void testGetAvatar_FileNotExists() {
        // Given
        byte[] defaultImg = hexToBytes(AvatarDefaultImg.DEFAULT.getImg());
        user.setAvatarId(null);

        // When
        byte[] avatar = userAvatarMgr.getAvatar(createAuthentication(user));

        // Then
        assertArrayEquals(defaultImg, avatar);
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}
