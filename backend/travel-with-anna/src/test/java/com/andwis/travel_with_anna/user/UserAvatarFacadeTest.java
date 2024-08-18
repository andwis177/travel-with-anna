package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.handler.exception.SaveAvatarException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.avatar.AvatarImg;
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
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
import static com.andwis.travel_with_anna.user.avatar.AvatarService.hexToBytes;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Avatar Service tests")
class UserAvatarFacadeTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserAvatarFacade userAvatarFacade;

    @Autowired
    private RoleRepository roleRepository;

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

        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrivedRole)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userRepository.save(user);
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
        userAvatarFacade.setAvatar(file, createAuthentication(user));

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
        SaveAvatarException exception = assertThrows(SaveAvatarException.class, () -> {
            userAvatarFacade.setAvatar(file, createAuthentication(user));
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
            userAvatarFacade.setAvatar(file, createAuthentication(user));
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

        user.setAvatarId(null);

        // When
        userAvatarFacade.setAvatar(file, createAuthentication(user));

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
        userAvatarFacade.setAvatar(file, createAuthentication(user));

        // When
        byte[] avatarBytes = userAvatarFacade.getAvatar(createAuthentication(user));

        // Then
        assertNotNull(avatarBytes);
        assertEquals("some-image-content", new String(avatarBytes));
    }

    @Test
    void testGetAvatar_DefaultAvatar(){
        // Given
        byte[] defaultImg = hexToBytes(AvatarImg.DEFAULT.getImg());
        // When
        byte[] avatar = userAvatarFacade.getAvatar(createAuthentication(user));
        // Then
        assertArrayEquals(defaultImg, avatar);
        assertNotNull(user.getAvatarId());
    }


    @Test
    void testGetAvatar_FileNotExists() {
        // Given
        byte[] defaultImg = hexToBytes(AvatarImg.DEFAULT.getImg());
        user.setAvatarId(null);
        // When
        byte[] avatar = userAvatarFacade.getAvatar(createAuthentication(user));
        // Then
        assertArrayEquals(defaultImg, avatar);
        assertNotNull(user.getAvatarId());
    }

    private Authentication createAuthentication(User user) {
        SecurityUser securityUser = new SecurityUser(user);
        return new UsernamePasswordAuthenticationToken(securityUser, user.getPassword(), securityUser.getAuthorities());
    }
}