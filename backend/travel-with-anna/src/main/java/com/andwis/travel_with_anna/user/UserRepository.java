package com.andwis.travel_with_anna.user;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String userEmail);

    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
}
