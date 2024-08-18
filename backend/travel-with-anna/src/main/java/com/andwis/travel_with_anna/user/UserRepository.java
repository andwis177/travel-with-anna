package com.andwis.travel_with_anna.user;

import com.andwis.travel_with_anna.role.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String userEmail);
    Optional<User> findByUserName(String userName);

    boolean existsByEmail(String email);
    boolean existsByUserName(String userName);
    boolean existsByRole(Role role);
}
