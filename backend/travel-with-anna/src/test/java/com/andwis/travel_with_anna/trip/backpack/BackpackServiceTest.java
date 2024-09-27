package com.andwis.travel_with_anna.trip.backpack;

import com.andwis.travel_with_anna.handler.exception.BackpackNotFoundException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.item.Item;
import com.andwis.travel_with_anna.trip.backpack.item.ItemRepository;
import com.andwis.travel_with_anna.trip.backpack.item.ItemWithExpanseRequest;
import com.andwis.travel_with_anna.trip.note.NoteRepository;
import com.andwis.travel_with_anna.trip.note.NoteService;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.trip.trip.TripRepository;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

import static com.andwis.travel_with_anna.role.Role.getUserAuthority;
import static com.andwis.travel_with_anna.role.Role.getUserRole;
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
    private NoteRepository noteRepository;

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

    private Backpack backpack;
    private Long backpackId;


    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setRoleName(getUserRole());
        role.setAuthority(getUserAuthority());
        Optional<Role> existingRole = roleRepository.findByRoleName(getUserRole());
        Role retrievedRole = existingRole.orElseGet(() -> roleRepository.save(role));

        User user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(passwordEncoder.encode("password"))
                .role(retrievedRole)
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
                .items(new HashSet<>())
                .build();

        backpack.setTrip(trip);
        backpackId = backpackRepository.save(backpack).getBackpackId();
    }

    @AfterEach
    void clean() {
        tripRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();
        backpackRepository.deleteAll();
        itemRepository.deleteAll();
        noteRepository.deleteAll();
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
        ItemWithExpanseRequest itemWithExpanseRequest = ItemWithExpanseRequest.builder()
                .itemName("Water Bottle")
                .build();

        // When
        backpackService.addItemToBackpack(backpackId, itemWithExpanseRequest);
        Backpack updatedBackpack = backpackRepository.findById(backpackId).orElseThrow();

        // Then
        assertEquals(1, updatedBackpack.getItems().size());
        assertEquals(1, updatedBackpack.getItems().size());

        Item item = itemRepository.findAll().getFirst();
        assertEquals("Water Bottle", item.getItemName());

    }

    @Test
    @Transactional
    void testGetBackpackById() {
        // Given
        boolean isNote = true;  // Mock note existence
        when(noteService.isNoteExists(backpackId)).thenReturn(isNote);

        // When
        BackpackResponse backpackResponse = backpackService.getBackpackById(backpackId);

        // Then
        assertNotNull(backpackResponse);
        assertEquals(backpackId, backpackResponse.backpackId());
        assertEquals(isNote, backpackResponse.isNote());
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
        backpackService.deleteItem(item.getItemId());
        entityManager.flush();
        Backpack updatedBackpack = backpackRepository.findById(backpackId).orElseThrow();

        // Then
        assertTrue(updatedBackpack.getItems().isEmpty());
        assertEquals(0, itemRepository.count());
    }
}