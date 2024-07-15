package com.andwis.travel_with_anna;

import com.andwis.travel_with_anna.auth.RegistrationRequest;
import com.andwis.travel_with_anna.handler.exception.UserExistsException;
import com.andwis.travel_with_anna.role.RoleRepository;
import com.andwis.travel_with_anna.user.User;
import com.andwis.travel_with_anna.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.RoleNotFoundException;
import java.util.List;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository,
									PasswordEncoder passwordEncoder, UserRepository userRepository) throws RoleNotFoundException {
		return args -> {
			if (userRepository.findByEmail("test@example.com").isEmpty()) {
				RegistrationRequest request = RegistrationRequest.builder().
						userName("userName").
						email("test@example.com")
						.password("password")
						.build();

				var userRole = roleRepository.findByRoleName("USER")
						.orElseThrow(() -> new RoleNotFoundException("Role 'USER' not found"));
				var user = User.builder()
						.userName(request.getUserName())
						.email(request.getEmail())
						.password(passwordEncoder.encode(request.getPassword()))
						.accountLocked(false)
						.enabled(true)
						.roles(List.of(userRole))
						.build();
				if (userRepository.existsByEmail(user.getEmail())) {
					throw new UserExistsException("User with this email already exists");
				}
				userRepository.save(user);
			}
		};
	}
}


