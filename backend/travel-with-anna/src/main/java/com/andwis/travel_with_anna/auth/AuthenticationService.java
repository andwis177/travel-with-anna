package com.andwis.travel_with_anna.auth;

import com.andwis.travel_with_anna.email.EmailService;
import com.andwis.travel_with_anna.handler.exception.ExpiredTokenException;
import com.andwis.travel_with_anna.handler.exception.InvalidTokenException;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.handler.exception.WrongPasswordException;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleService;
import com.andwis.travel_with_anna.security.JwtService;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserCredentialsResponse;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import com.andwis.travel_with_anna.user.token.Token;
import com.andwis.travel_with_anna.user.token.TokenRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

import static com.andwis.travel_with_anna.role.Role.getAdminRole;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AvatarService avatarService;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Value("${application.mailing.frontend.login-url}")
    private String loginUrl;

    @Transactional
    public void register(@NotNull RegistrationRequest request) throws RoleNotFoundException, MessagingException, WrongPasswordException {
        Role role = roleService.getRoleByName(request.getRoleName());

        if (role.getRoleName().equals(getAdminRole())) {
            if (userService.existsByRoleName(getAdminRole())) {
                throw new UserExistsException("Admin already exists");
            }
        }
        var userRole = roleService.getRoleByName(request.getRoleName());
        var user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .role(userRole)
                .build();

        if (userService.existsByEmail(user.getEmail())) {
            throw new UserExistsException("User with this email already exists");
        }
        if (userService.existsByUserName(user.getUserName())) {
            throw new UserExistsException("User with this name already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new WrongPasswordException("Passwords do not match");
        }
        avatarService.createAvatar(user);
        userService.saveUser(user);

           sendValidationEmail(user);
        System.out.println(generateAndSaveActivationToken(user));
    }


    private void sendValidationEmail(User user) throws MessagingException {
        var newToken = generateAndSaveActivationToken(user);
        emailService.sendValidationEmail(
                user.getEmail(),
                user.getUserName(),
                activationUrl,
                newToken,
                "Account activation"
        );
    }

    private @NotNull String generateAndSaveActivationToken(User user) {
        String generatedToken = generateCode(6);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .user(user)
                .build();
        tokenRepository.save(token);
        return generatedToken;
    }

    private @NotNull String generateCode(int length) {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    private AuthenticationResponse authenticate(@NotNull AuthenticationRequest request) throws WrongPasswordException {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );
            var jwtToken = jwtService.generateJwtToken(auth);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (AuthenticationException exp) {
            throw new WrongPasswordException("Authentication failed: Invalid email or password");
        }
    }

    public AuthenticationResponse authenticationWithCredentials(AuthenticationRequest request) throws WrongPasswordException {
        AuthenticationResponse response = authenticate(request);
        UserCredentialsResponse userCredentials = userService.getCredentials(request.getEmail());
        response.setUserName(userCredentials.userName());
        response.setEmail(userCredentials.email());
        response.setRole(userCredentials.role());
        return response;
    }

    @Transactional
    public void activateAccount(String token) throws MessagingException {
        var tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        if (LocalDateTime.now().isAfter((tokenEntity.getExpiresAt()))) {
//
                      sendValidationEmail(tokenEntity.getUser());

            throw new ExpiredTokenException("Token expired. New token was sent to your email");
        }
        var user = userService.getUserById(tokenEntity.getUser().getUserId());
        user.setEnabled(true);
        userService.saveUser(user);
        tokenRepository.delete(tokenEntity);
    }

    private @NotNull String generateAndSaveNewPassword(@NotNull User user) {
        String generatedPassword = generateCode(20);
        user.setPassword(passwordEncoder.encode(generatedPassword));
        userService.saveUser(user);
        return generatedPassword;
    }

    @Transactional
    protected void resetPassword(@NotNull ResetPasswordRequest request) throws MessagingException {
        User user;
        if (request.getCredential().contains("@")) {
            user = userService.getUserByEmail(request.getCredential());
        } else {
            user = userService.getUserByUserName(request.getCredential());
        }
        String newPassword = generateAndSaveNewPassword(user);
        emailService.sendResetPassword(
                user.getEmail(),
                user.getUserName(),
                loginUrl,
                newPassword,
                "Password reset"
        );
    }
}
