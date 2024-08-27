package com.andwis.travel_with_anna.user.avatar;

import com.andwis.travel_with_anna.handler.exception.AvatarNotFoundException;
import com.andwis.travel_with_anna.user.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@DisplayName("Avatar Service tests")
class AvatarServiceTest {
    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private AvatarService avatarService;

    @AfterEach
    void tearDown() {
        avatarRepository.deleteAll();
    }

    @Test
    void save_ShouldReturnSavedAvatar() {
        // Given
        Avatar avatar = new Avatar();

        // When
        Avatar savedAvatar = avatarService.saveAvatar(avatar);

        // Then
        assertNotNull(savedAvatar);
        assertEquals(avatar, savedAvatar);
    }

    @Test
    void existsById_ShouldReturnTrue_WhenAvatarExists() {
        // Given
        Avatar avatar = new Avatar();
        Avatar savedAvatar = avatarService.saveAvatar(avatar);

        // When
        boolean result = avatarService.existsById(savedAvatar.getAvatarId());

        // Then
        assertTrue(result);
    }

    @Test
    void existsById_ShouldReturnFalse_WhenAvatarDoesNotExist() {
        // Given
        // When
        boolean result = avatarService.existsById(999L);
        // Then
        assertFalse(result);
    }

    @Test
    void findById_ShouldReturnAvatar_WhenAvatarExists() {
        // Given
        Avatar avatar = new Avatar();
        Avatar savedAvatar = avatarService.saveAvatar(avatar);

        // When
        Avatar result = avatarService.findById(savedAvatar.getAvatarId());

        // Then
        assertNotNull(result);
        assertEquals(savedAvatar, result);
    }

    @Test
    void findById_ShouldThrowException_WhenAvatarDoesNotExist() {
        // Given
        // When & Then
        assertThrows(AvatarNotFoundException.class, () -> avatarService.findById(999L));
    }

    @Test
    void createAvatar_ShouldReturnCreatedAvatar() {
        // Given
        User user = new User();

        // When
        Avatar avatar = avatarService.createAvatar(user);

        // Then
        assertNotNull(avatar);
        assertEquals(avatar.getAvatarId(), user.getAvatarId());
    }

    @Test
    void deleteAvatar_ShouldDeleteAvatar_WhenAvatarIdExists() {
        // Given
        User user = new User();
        Avatar avatar = avatarService.createAvatar(user);
        user.setAvatarId(avatar.getAvatarId());

        // When
        avatarService.deleteAvatar(user);

        // Then
        assertNull(user.getAvatarId());
    }

    @Test
    void deleteAvatar_ShouldNotDeleteAvatar_WhenAvatarIdDoesNotExist() {
        // Given
        User user = new User();

        // When
        avatarService.deleteAvatar(user);

        // Then
        assertNull(user.getAvatarId());
    }
}