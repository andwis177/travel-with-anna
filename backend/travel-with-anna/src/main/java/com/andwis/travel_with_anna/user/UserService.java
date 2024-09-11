package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.auth.AuthenticationResponse;
import com.andwis.travel_with_anna.handler.exception.EmailNotFoundException;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.handler.exception.UserNotFoundException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.security.JwtService;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final AvatarService avatarService;

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

    public User getConnectedUser(Authentication connectedUser) {
        var securityUser = getSecurityUser(connectedUser);
        return securityUser.getUser();
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new EmailNotFoundException("User not found"));
    }

    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName).orElseThrow(() ->
                new UsernameNotFoundException("User not found"));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException("User not found"));
    }

    public UserCredentials getCredentials(String email) {
        User user = getUserByEmail(email);
        return UserCredentials.builder()
                .email(user.getEmail())
                .userName(user.getUserName())
                .role(user.getRole().getRoleName())
                .build();
    }

    public AuthenticationResponse updateUserExecution(UserCredentials userCredentials, Authentication connectedUser) {
        var user = getSecurityUser(connectedUser).getUser();
        verifyPassword(user, userCredentials.getPassword());
        updateUser(userCredentials, user);
        updateSecurityContext(userCredentials.getEmail());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String newJwtToken = jwtService.generateJwtToken(authentication);
        return AuthenticationResponse.builder()
                .token(newJwtToken)
                .userName(userCredentials.getUserName())
                .email(userCredentials.getEmail())
                .build();
    }


    private void updateUser(UserCredentials userCredentials, User user) {
        boolean isChanged = false;
        String newUserName = userCredentials.getUserName();
        String newEmail = userCredentials.getEmail();

        if (newEmail != null
                && !newEmail.isBlank()
                && !newEmail.equals(user.getEmail())) {
            if (userRepository.existsByEmail(newEmail)) {
                throw new UserExistsException("Email already exists");
            } else {
                user.setEmail(newEmail);
                isChanged = true;
            }
        }
        if (newUserName != null
                && !newUserName.isBlank()
                && !newUserName.equals(user.getUserName())) {
            if (userRepository.existsByUserName(newUserName)) {
                throw new UserExistsException("User with this name already exists");
            } else {
                user.setUserName(newUserName);
                isChanged = true;
            }
        }
        if (isChanged) {
            userRepository.save(user);
        }
    }

    private void updateSecurityContext(String userName) {
        SecurityUser user = (SecurityUser) userDetailsService.loadUserByUsername(userName);
        if (user == null) {
            throw new UsernameNotFoundException("User is logged out");
        }
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(user, user.getPassword(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    public UserRespond changePassword(ChangePasswordRequest request, Authentication connectedUser) {
        var user = getSecurityUser(connectedUser);
        var currentUser = user.getUser();
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new WrongPasswordException("Passwords do not match");
        }

        verifyPassword(currentUser, request.getCurrentPassword());
        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        return UserRespond.builder()
                .message("Password has been changed")
                .build();
    }

    public UserRespond deleteConnectedUser(PasswordRequest request, Authentication connectedUser)
            throws UsernameNotFoundException, WrongPasswordException {
        var securityUser = getSecurityUser(connectedUser);
        var currentUser = securityUser.getUser();
        String userName = currentUser.getUserName();
        verifyPassword(currentUser, request.password());
        userRepository.delete(securityUser.getUser());
        avatarService.deleteAvatar(currentUser);
        return UserRespond.builder()
                .message("User " + userName + " has been deleted!")
                .build();
    }

    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    public void verifyPassword(User user, String password) throws WrongPasswordException {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), password));
        } catch (WrongPasswordException exp) {
            throw new WrongPasswordException("Wrong password");
        }
    }

    private static SecurityUser getSecurityUser(Authentication connectedUser) throws UsernameNotFoundException {
        var securityUser  = (SecurityUser) connectedUser.getPrincipal();
        if (securityUser  == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return securityUser ;
    }

}