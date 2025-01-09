package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemRepository;
import com.andwis.travel_with_anna.trip.backpack.item.ItemRequest;
import com.andwis.travel_with_anna.trip.note.NoteService;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@DisplayName("Backpack Service tests")
class BackpackServiceTest {
    @Autowired
    private BackpackService backpackService;
    @Autowired
    private BackpackRepository backpackRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private EntityManager entityManager;
    @MockBean
    private NoteService noteService;
    private UserDetails userDetails;
    private Backpack backpack;
    private Long backpackId;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByRoleName(USER.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(1, USER.getRoleName(), USER.getAuthority())));

        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(role)
                .avatarId(1L)
                .build();
        user.setEnabled(true);
        userRepository.save(user);

        Trip trip = Trip.builder()
                .tripName("Trip to Paris")
                .owner(user)
                .build();

        tripRepository.save(trip);

        backpack = Backpack.builder()
                .backpackItems(new ArrayList<>())
                .build();

        backpack.setTrip(trip);
        backpackId = backpackRepository.save(backpack).getBackpackId();

        userDetails = createUserDetails(user);
    }

    @AfterEach
    void clean() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    void testFindBackpackById() {
        // When
        Backpack foundBackpack = backpackService.findById(backpackId);

        // Then
        assertNotNull(foundBackpack);
        assertEquals(backpackId, foundBackpack.getBackpackId());
    }

    @Test
    void testThrowExceptionWhenBackpackNotFound() {
        // Given
        Long nonExistentId = 999L;

        // When & Then
        assertThrows(BackpackNotFoundException.class, () -> backpackService.findById(nonExistentId));
    }

    @Test
    @Transactional
    void testAddItemToBackpack() {
        // Given
        ItemRequest itemRequest = ItemRequest.builder()
                .itemName("Water Bottle")
                .build();

        // When
        backpackService.addItemToBackpack(backpackId, itemRequest, userDetails);
        Backpack updatedBackpack = backpackRepository.findById(backpackId).orElseThrow();

        // Then
        assertEquals(1, updatedBackpack.getBackpackItems().size());
        assertEquals(1, updatedBackpack.getBackpackItems().size());

        Item item = itemRepository.findAll().getFirst();
        assertEquals("Water Bottle", item.getItemName());
    }

    @Test
    @Transactional
    void testGetBackpackById() {
        // Given
        boolean isNote = true;
        when(noteService.isNoteExists(backpackId)).thenReturn(isNote);

        // When
        BackpackResponse backpackResponse = backpackService.getBackpackById(backpackId, userDetails);

        // Then
        assertNotNull(backpackResponse);
        assertEquals(backpackId, backpackResponse.backpackId());
        assertEquals(isNote, backpackResponse.noteExists());
    }

    @Transactional
    @Test
    void testDeleteItem() {
        // Given
        Item item = Item.builder()
                .itemName("Sleeping Bag")
                .build();
        backpack.addItem(item);
        itemRepository.save(item);
        backpackRepository.save(backpack);

        // When
        backpackService.deleteItem(item.getItemId(), userDetails);
        entityManager.flush();
        Backpack updatedBackpack = backpackRepository.findById(backpackId).orElseThrow();

        // Then
        assertTrue(updatedBackpack.getBackpackItems().isEmpty());
        assertEquals(0, itemRepository.count());
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