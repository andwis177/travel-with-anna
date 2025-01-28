package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.address.AddressService;
import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.EmailNotFoundException;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.handler.exception.UserNotFoundException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.security.JwtService;
import com.andwis.travel_with_anna.trip.day.Day;
import com.andwis.travel_with_anna.trip.day.DayService;
import com.andwis.travel_with_anna.trip.trip.Trip;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableJpaAuditing
public class UserService {

    private static final String EMAIL_EXISTS_ERROR = "Email already exists";
    private static final String USERNAME_EXISTS_ERROR = "User with this name already exists";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserAuthenticationService userAuthenticationService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;
    private final AddressService addressService;
    private final DayService dayService;

    public Long saveUser(User user) {
        return userRepository.save(user).getUserId();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    public boolean existsByRoleName(String roleName) {
        if (roleRepository.existsByRoleName(roleName)) {
            Role role = roleRepository.findByRoleName(roleName).orElseThrow(() ->
                    new UserNotFoundException("Role not found"));
            return userRepository.existsByRole(role);
        }
        return false;
    }

    public boolean existsByUserId(Long userId) {
        return userRepository.existsById(userId);
    }

    public Page<User> getAllUsersExcept(Pageable pageable, Long userId) {
        return userRepository.findAllExcept(pageable, userId);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EmailNotFoundException("User not found"));
    }

    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(() ->
                new UserNotFoundException("User not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User not found"));
    }

    public UserCredentialsResponse getCredentials(String email) {
        User user = getUserByEmail(email);
        return new UserCredentialsResponse(
                user.getEmail(),
                user.getUserName(),
                user.getRole().getRoleName()
        );
    }

    @Transactional
    public AuthenticationResponse updateUserDetails(
            @NotNull UserCredentialsRequest userCredentials, UserDetails connectedUser)
            throws WrongPasswordException {
        var user = userAuthenticationService.retriveConnectedUser(connectedUser);
        userAuthenticationService.verifyPassword(user, userCredentials.getPassword());
        processUserUpdates(userCredentials, user);
        userAuthenticationService.updateSecurityContext(userCredentials.getEmail());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String newJwtToken = jwtService.generateJwtToken(authentication);
        return AuthenticationResponse.builder()
                .token(newJwtToken)
                .userName(userCredentials.getUserName())
                .email(userCredentials.getEmail())
                .build();
    }

    private void processUserUpdates(@NotNull UserCredentialsRequest userCredentials, User user) {
        boolean emailUpdated = updateEmail(userCredentials.getEmail(), user);
        boolean usernameUpdated = updateUsername(userCredentials.getUserName(), user);

        if (emailUpdated || usernameUpdated) {
            userRepository.save(user);
        }
    }

    private boolean updateEmail(String newEmail, User user) {
        if (newEmail != null && !newEmail.isBlank() && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new UserExistsException(EMAIL_EXISTS_ERROR);
            }
            user.setEmail(newEmail);
            return true;
        }
        return false;
    }

    private boolean updateUsername(String newUserName, User user) {
        if (newUserName != null && !newUserName.isBlank() && !newUserName.equals(user.getUserName())) {
            if (userRepository.existsByUserName(newUserName)) {
                throw new UserExistsException(USERNAME_EXISTS_ERROR);
            }
            user.setUserName(newUserName);
            return true;
        }
        return false;
    }

    @Transactional
    public UserResponse changePassword(@NotNull ChangePasswordRequest request, UserDetails connectedUser) throws WrongPasswordException {
        var currentUser = userAuthenticationService.retriveConnectedUser(connectedUser);
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new WrongPasswordException("Passwords do not match");
        }

        userAuthenticationService.verifyPassword(currentUser, request.getCurrentPassword());
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        return UserResponse.builder()
                .message("Password has been changed")
                .build();
    }

    @Transactional
    public UserResponse deleteConnectedUser(@NotNull PasswordRequest request, UserDetails connectedUser)
            throws UsernameNotFoundException, WrongPasswordException {
        var currentUser = userAuthenticationService.retriveConnectedUser(connectedUser);
        String userName = currentUser.getUserName();
        userAuthenticationService.verifyPassword(currentUser, request.password());
        deleteUser(currentUser);
        return UserResponse.builder()
                .message("User " + userName + " has been deleted!")
                .build();
    }

    @Transactional
    public void deleteUser(User user) {
        Set<Day> days = getAllUserTripDays(user);
        Set<Long> addressIds = addressService.extractAddressIdsFromDays(days);

        avatarService.deleteAvatar(user);
        userRepository.delete(user);
        addressService.deleteExistingAddressesByIds(addressIds);
    }

    private Set<Day> getAllUserTripDays(@NotNull User user) {
        Set<Trip> trips = user.getTrips();
        Set<Long> tripIds = trips.stream().map(Trip::getTripId).collect(Collectors.toSet());
        return dayService.getDaysByTripIds(tripIds);
    }
}