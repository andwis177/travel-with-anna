package com.andwis.travel_with_anna.user.token;

import com.andwis.travel_with_anna.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long>{
    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User user);
    boolean existsByUser(User user);
}
