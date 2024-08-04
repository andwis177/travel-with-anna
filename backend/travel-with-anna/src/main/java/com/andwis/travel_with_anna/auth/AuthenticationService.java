package com.andwis.travel_with_anna.auth;

import com.andwis.travel_with_anna.email.EmailService;
import com.andwis.travel_with_anna.handler.exception.*;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.security.JwtService;
import com.andwis.travel_with_anna.user.Token;
import com.andwis.travel_with_anna.user.TokenRepository;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.management.relation.RoleNotFoundException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    @Value("${application.mailing.frontend.activation-url}")
    private String activationUrl;
    @Value("${application.mailing.frontend.login-url}")
    private String loginUrl;

    public void register(RegistrationRequest request) throws RoleNotFoundException, MessagingException {
        var userRole = roleRepository.findByRoleName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role 'USER' not found"));
        var user = User.builder()
                .userName(request.getUserName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(false)
                .roles(List.of(userRole))
                .build();
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserExistsException("User with this email already exists");
        }
        if (userRepository.existsByUserName(user.getUserName())) {
            throw new UserExistsException("User with this name already exists");
        }
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new WrongPasswordException("Passwords do not match");
        }

        userRepository.save(user);
//        sendValidationEmail(user);
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

    private String generateAndSaveActivationToken(User user) {
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

    private String generateCode(int length) {
        final String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder codeBuilder = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();

        for(int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            codeBuilder.append(characters.charAt(randomIndex));
        }
        return codeBuilder.toString();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) throws WrongPasswordException {
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

    public void activateAccount(String token) throws MessagingException {
        var tokenEntity = tokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidTokenException("Invalid token"));
        if (LocalDateTime.now().isAfter((tokenEntity.getExpiresAt()))) {

//            sendValidationEmail(tokenEntity.getUser());
            throw new ExpiredTokenException("Token expired. New token was sent to your email");
        }
        var user = userRepository.findById(tokenEntity.getUser().getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(tokenEntity);
    }

    private String generateAndSaveNewPassword(User user) {
        String generatedPassword = generateCode(20);
        user.setPassword(passwordEncoder.encode(generatedPassword));
        userRepository.save(user);
        return generatedPassword;
    }

    protected void resetPassword(ResetPasswordRequest request) throws MessagingException {
        User user;
        if (request.getCredential().contains("@")) {
            user = userRepository.findByEmail(request.getCredential())
                    .orElseThrow(() -> new EmailNotFoundException("User with this email not found"));
        } else {
            user = userRepository.findByUserName(request.getCredential())
                    .orElseThrow(() -> new UsernameNotFoundException("User with this user name not found"));
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
