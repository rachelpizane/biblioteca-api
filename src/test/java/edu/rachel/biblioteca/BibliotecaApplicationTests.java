package edu.rachel.biblioteca;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
class BibliotecaApplicationTests {

	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
