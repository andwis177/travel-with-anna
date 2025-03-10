package com.andwis.travel_with_anna.auth;

import com.andwis.travel_with_anna.email.EmailDetails;
import com.andwis.travel_with_anna.email.EmailService;
import com.andwis.travel_with_anna.email.EmailTemplateName;
import com.andwis.travel_with_anna.handler.exception.*;
import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleService;
import com.andwis.travel_with_anna.role.RoleType;
import com.andwis.travel_with_anna.security.JwtService;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserCredentialsResponse;
import com.andwis.travel_with_anna.user.UserService;
import com.andwis.travel_with_anna.user.avatar.AvatarService;
import com.andwis.travel_with_anna.user.token.Token;
import com.andwis.travel_with_anna.user.token.TokenService;
import jakarta.mail.MessagingException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleNotFoundException;
import javax.security.auth.login.AccountLockedException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class AuthenticationService {

    private static final int PASSWORD_LENGTH = 20;
    private static final int TOKEN_LENGTH = 6;
    private static final int TOKEN_EXPIRATION_TIME = 10;
    private static final String ACTIVATION_EMAIL_SUBJECT = "Activation Code";
    private static final String RESET_PASSWORD_EMAIL_SUBJECT = "New password";
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    @Value("${spring.mail.sender_email}")
    private String senderEmail;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;
    private final AvatarService avatarService;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmailService emailService;

    @Autowired
    public AuthenticationService(RoleService roleService,
                                 PasswordEncoder passwordEncoder,
                                 UserService userService,
                                 AvatarService avatarService,
                                 TokenService tokenService,
                                 AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 @Qualifier("GmailApiEmailSender") EmailService emailService) {
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.avatarService = avatarService;
        this.tokenService = tokenService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.emailService = emailService;
    }

    @Transactional
    public void register(@NotNull RegistrationRequest request) throws Exception {
        validateRegistrationRequest(request);

        var userRole = roleService.getRoleByName(request.getRoleName());
        var user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .role(userRole)
                .build();

        avatarService.createAvatar(user);
        userService.saveUser(user);
        sendValidationEmail(user);
    }

    private void validateRegistrationRequest(@NotNull RegistrationRequest request) throws RoleNotFoundException, WrongPasswordException {
        Role role = roleService.getRoleByName(request.getRoleName());
        if (role.getRoleName().equals(RoleType.ADMIN.getRoleName()) && userService.existsByRoleName(RoleType.ADMIN.getRoleName())) {
            throw new UserExistsException("Admin already exists");
        }
        if (userService.existsByEmail(request.getEmail())) {
            throw new UserExistsException("User with this email already exists");
        }
        if (userService.existsByUserName(request.getUserName())) {
            throw new UserExistsException("User with this name already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new WrongPasswordException("Passwords do not match");
        }
    }

    private void sendValidationEmail(@NotNull User user) throws Exception {
        var newToken = generateAndSaveActivationToken(user);
        EmailDetails details = new EmailDetails(
                user.getEmail(),
                user.getUserName(),
                senderEmail,
                newToken,
                ACTIVATION_EMAIL_SUBJECT,
                EmailTemplateName.ACTIVATE_ACCOUNT
        );
        emailService.sendEmail(details);
    }

    private @NotNull String generateAndSaveActivationToken(User user) {
        String generatedToken = generateRandomCode(AuthenticationService.TOKEN_LENGTH);
        var token = Token.builder()
                .token(generatedToken)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(TOKEN_EXPIRATION_TIME))
                .user(user)
                .build();
        tokenService.saveToken(token);
        return generatedToken;
    }

    private @NotNull String generateRandomCode(int length) {
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(CHARACTERS.length());
            codeBuilder.append(CHARACTERS.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    private AuthenticationResponse authenticate(@NotNull AuthenticationRequest request) throws WrongPasswordException, AccountLockedException {
        try {
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(), request.getPassword())
            );
            var jwtToken = jwtService.generateJwtToken(auth);
            return AuthenticationResponse.builder()
                    .token(jwtToken)
                    .build();
        } catch (LockedException e) {
            throw new AccountLockedException("Authentication failed: Account is locked");
        } catch (DisabledException e) {
            throw new DisabledAccountException("Authentication failed: Account is disabled");
        } catch (AuthenticationException e) {
            throw new WrongPasswordException("Authentication failed: Invalid email or password");
        }
    }

    public AuthenticationResponse authenticationWithCredentials(AuthenticationRequest request) throws WrongPasswordException, AccountLockedException {
        AuthenticationResponse response = authenticate(request);
        UserCredentialsResponse userCredentials = userService.getCredentials(request.getEmail());
        response.setUserName(userCredentials.userName());
        response.setEmail(userCredentials.email());
        response.setRole(userCredentials.role());
        return response;
    }

    public void activateAccount(String token) throws Exception {
        var tokenEntity = tokenService.getByToken(token);
        if (LocalDateTime.now().isAfter((tokenEntity.getExpiresAt()))) {
            afterTokenExpired(tokenEntity);
        }
        afterTokenAccepted(tokenEntity);
    }

    protected void afterTokenExpired(@NotNull Token token) throws Exception {
        User retrivedUser = token.getUser();
        token.setUser(null);
        tokenService.deleteToken(token);
        sendValidationEmail(retrivedUser);
        throw new ExpiredTokenException("Token expired. New token was sent to user email address");
    }

    protected void afterTokenAccepted(@NotNull Token token){
        var user = userService.getUserById(token.getUser().getUserId());
        user.setEnabled(true);
        userService.saveUser(user);
        token.setUser(null);
        tokenService.deleteToken(token);
    }

    private @NotNull String generateAndSaveNewPassword(@NotNull User user) {
        String generatedPassword = generateRandomCode(PASSWORD_LENGTH);
        user.setPassword(passwordEncoder.encode(generatedPassword));
        userService.saveUser(user);
        return generatedPassword;
    }

    @Transactional
    protected void resetPassword(@NotNull ResetPasswordRequest request) throws MessagingException, GeneralSecurityException, IOException, InterruptedException {
        User user;
        if (request.getCredential().contains("@")) {
            user = userService.getUserByEmail(request.getCredential());
        } else {
            user = userService.getUserByUserName(request.getCredential());
        }
            String newPassword = generateAndSaveNewPassword(user);
            EmailDetails details = new EmailDetails(
                    user.getEmail(),
                    user.getUserName(),
                    senderEmail,
                    newPassword,
                    RESET_PASSWORD_EMAIL_SUBJECT,
                    EmailTemplateName.RESET_PASSWORD
            );
            emailService.sendEmail(details);
    }

    @Transactional
    public void sendActivationCodeByRequest(String email) throws Exception {
        User user = userService.getUserByEmail(email);
        validateActivationCodeRequest(user);
        sendValidationEmail(user);
    }

    @Transactional
    protected void validateActivationCodeRequest(@NotNull User user) throws InvalidTokenException {
        if (user.isEnabled()) {
            throw new UserIsActiveException("User is already activated");
        }
        if (tokenService.isTokenExists(user)) {
            var tokenEntity = tokenService.getByUser(user);
            tokenService.deleteToken(tokenEntity);
        }
    }
}
