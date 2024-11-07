package com.andwis.travel_with_anna;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TravelWithAnnaApiApplicationTests {

	@Autowired
	private Flyway flyway;

	@BeforeEach
	public void setUp() {
		flyway.clean();
		flyway.migrate();
	}

	@Test
	void contextLoads() {
	}

}
