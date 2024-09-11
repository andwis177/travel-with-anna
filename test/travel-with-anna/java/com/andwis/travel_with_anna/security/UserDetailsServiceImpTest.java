package com.andwis.travel_with_anna.security;

import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("User Details Service Implementation tests")
class UserDetailsServiceImpTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImp userService;
    private User user;

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .email("existing@example.com")
                .build();

        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.empty());
    }

    @Test
    public void testLoadUserByUsername_existingUser() {
        //Given
        when(userRepository.findByEmail("existing@example.com"))
                .thenReturn(Optional.of(user));

        //When
        UserDetails userDetails = userService.loadUserByUsername("existing@example.com");

        //Then
        assertNotNull(userDetails);
        assertEquals("existing@example.com", userDetails.getUsername());
    }

    @Test
    public void testLoadUserByUsername_nonExistingUser() {
        //Given
        when(userRepository.findByEmail("nonexisting@example.com"))
                .thenReturn(Optional.empty());
        //When & Then
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("nonexisting@example.com");
        });
    }


}