package com.andwis.travel_with_anna;

import com.andwis.travel_with_anna.role.Role;
import com.andwis.travel_with_anna.role.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

import static com.andwis.travel_with_anna.role.RoleType.ADMIN;
import static com.andwis.travel_with_anna.role.RoleType.USER;

@EnableScheduling
@SpringBootApplication
@EnableAsync
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner initializeDatabase(RoleRepository roleRepository) {
		return args -> {
			if (roleRepository.count() == 0) {
				Role userRole = Role.builder()
						.roleName(USER.getRoleName())
						.roleAuthority(USER.getAuthority())
						.build();

				Role adminRole = Role.builder()
						.roleName(ADMIN.getRoleName())
						.roleAuthority(ADMIN.getAuthority())
						.build();

				roleRepository.saveAll(List.of(userRole, adminRole));
			}
		};
	}
}


