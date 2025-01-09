package com.andwis.travel_with_anna.trip.note;

import com.andwis.travel_with_anna.handler.exception.NoteTypeException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.trip.backpack.Backpack;
import com.andwis.travel_with_anna.trip.budget.Budget;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayRepository;
import com.andwis.travel_with_anna.trip.day.activity.Activity;
import com.andwis.travel_with_anna.trip.day.activity.ActivityRepository;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.SecurityUser;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;

import static com.andwis.travel_with_anna.role.RoleType.USER;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("Note Facade Tests")
class NoteFacadeTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private NoteFacade noteFacade;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DayRepository dayRepository;
    private User user;
    private Note note;
    private Day retrivedDay;
    private Long dayId;
    private Long dayNoteId;
    private Long activityId;
    private Long activityNoteId;

    @BeforeEach
    void setUp() {
        Role role = roleRepository.findByRoleName(USER.getRoleName())
                .orElseGet(() -> roleRepository.save(new Role(1, USER.getRoleName(), USER.getAuthority())));

        String encodedPassword = passwordEncoder.encode("password");
        user = User.builder()
                .userName("userName")
                .email("email@example.com")
                .password(encodedPassword)
                .role(role)
                .avatarId(1L)
                .trips(new HashSet<>())
                .build();
        user.setEnabled(true);

        Budget budget = Budget.builder()
                .currency("USD")
                .budgetAmount(BigDecimal.valueOf(1000))
                .build();

        note = Note.builder()
                .content("Sample note")
                .build();

        Activity activity = Activity.builder()
                .activityTitle("Sample activity")
                .build();
        activity.addNote(note);
        activityId = activityRepository.save(activity).getActivityId();

        Day day = Day.builder()
                .date(LocalDate.now())
                .activities(new HashSet<>())
                .build();
        day.addNote(note);
        day.addActivity(activity);
        dayId = dayRepository.save(day).getDayId();

        Trip trip = Trip.builder()
                .tripName("Initial Trip")
                .days(new HashSet<>())
                .build();
        trip.addBackpack(new Backpack());
        trip.addBudget(budget);
        trip.addDay(day);

        user.addTrip(trip);
        userRepository.save(user);

        Trip retrivedTrip = user.getTrips().stream().findFirst().orElse(null);
        assert retrivedTrip != null;
        retrivedDay = retrivedTrip.getDays().stream().findFirst().orElse(null);
        assert retrivedDay != null;
        dayNoteId = retrivedDay.getNote().getNoteId();
        Activity retrivedActivity = retrivedDay.getActivities().stream().findFirst().orElse(null);
        assert retrivedActivity != null;
        activityNoteId = retrivedActivity.getNote().getNoteId();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        roleRepository.deleteAll();
    }

    @Test
    @Transactional
    void testSaveNoteWithValidDayEntity() {
        // Given
        UserDetails connectedUser = createUserDetails(user);
        NoteRequest noteRequest = NoteRequest.builder()
                .noteId(dayNoteId)
                .linkedEntityId(retrivedDay.getDayId())
                .noteContent("Request day note")
                .linkedEntityType("day")
                .build();

        // When
        noteFacade.saveNote(noteRequest, connectedUser);

        // Then
        Note retrivedNote = retrivedDay.getNote();
        assertNotNull(retrivedNote);
        assertEquals("Request day note", retrivedNote.getContent());
        assertEquals(retrivedDay.getDayId(), retrivedNote.getDay().getDayId());
    }

    @Test
    @Transactional
    void testSaveNoteWithValidActivityEntity() {
        // Given
        UserDetails connectedUser = createUserDetails(user);
        NoteRequest noteRequest = NoteRequest.builder()
                .noteId(activityNoteId)
                .linkedEntityId(retrivedDay.getDayId())
                .noteContent("Request activity note")
                .linkedEntityType("activity")
                .build();

        // When
        noteFacade.saveNote(noteRequest, connectedUser);

        // Then
        Note retrivedNote = retrivedDay.getNote();
        assertNotNull(retrivedNote);
        assertEquals("Request activity note", retrivedNote.getContent());
        assertEquals(retrivedDay.getDayId(), retrivedNote.getDay().getDayId());
    }

    @Test
    @Transactional
    void testSaveNoteWithInvalidType() {
        // Given
        NoteRequest noteRequest = NoteRequest.builder()
                .noteContent("Request note")
                .linkedEntityType("invalid_type")
                .build();

        // When
        NoteTypeException exception = assertThrows(NoteTypeException.class, () ->
            noteFacade.saveNote(noteRequest, createUserDetails(user)));

        // Then
        assertTrue(exception.getMessage().contains("Invalid note type"));
    }

    @Test
    @Transactional
    void testGetNoteForDayEntity() {
        // Given
        // When
        NoteResponse actualResponse = noteFacade.getNote(dayId, "day", createUserDetails(user));

        // Then
        assertNotNull(actualResponse);
        assertEquals(note.getNoteId(), actualResponse.noteId());
        assertEquals(note.getContent(), actualResponse.note());
    }

    @Test
    @Transactional
    void testGetNoteForActivityEntity() {
        // Given
        // When
        NoteResponse actualResponse = noteFacade.getNote(activityId, "activity", createUserDetails(user));

        // Then
        assertNotNull(actualResponse);
        assertEquals(note.getNoteId(), actualResponse.noteId());
        assertEquals(note.getContent(), actualResponse.note());
    }

    @Test
    @Transactional
    void testGetNoteWithInvalidType() {
        // Given
        Long invalidEntityId = 30L;

        // When
        NoteResponse response = noteFacade.getNote(invalidEntityId, "invalid_type", createUserDetails(user));

        // Then
        assertNotNull(response);
        assertEquals(-1L, response.noteId());
        assertEquals("", response.note());
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