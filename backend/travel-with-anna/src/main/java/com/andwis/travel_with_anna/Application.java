package com.andwis.travel_with_anna;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.LocalDateTime;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner runner(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.findByRoleName("ADMIN").isEmpty()) {
				roleRepository.save(Role.builder()
						.roleName("ADMIN")
						.createdDate(LocalDateTime.now())
						.build());
			}
			if (roleRepository.findByRoleName("USER").isEmpty()) {
				roleRepository.save(Role.builder()
						.roleName("USER")
						.createdDate(LocalDateTime.now())
						.build());
			}
		};
	}
}
