package com.andwis.travel_with_anna.user.token;

import com.andwis.travel_with_anna.handler.exception.InvalidTokenException;
import com.andwis.travel_with_anna.user.User;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final TokenRepository tokenRepository;

    public void saveToken(Token token) {
        tokenRepository.save(token);
    }

    public boolean isTokenExists(User user) {
       return tokenRepository.existsByUser(user);
    }

    public Token getByToken(String token) {
        return tokenRepository.findByToken(token).orElseThrow(
                () -> new InvalidTokenException("Invalid token"));
    }

    public Token getByUser(User user) {
        return tokenRepository.findByUser(user).orElseThrow(
                () -> new InvalidTokenException("Invalid token"));
    }

    private @NotNull List<Token> getAllTokens() {
        return tokenRepository.findAll();
    }

    @Transactional
    protected Set<Token> getExpiredTokens() {
        List<Token> tokens = getAllTokens();
        return tokens.stream().filter(Token::isTokenExpired).collect(Collectors.toSet());
    }

    public void deleteToken(Token token) {
        tokenRepository.delete(token);
    }

    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteAll(getExpiredTokens());
    }
}
