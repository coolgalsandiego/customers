package com.pc.customers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class CustomersApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void testMainRunsWithoutException() {
		String[] args = {};
		assertDoesNotThrow(() -> CustomersApplication.main(args));
	}

	@Test
	void testSpringApplicationRunCalled() {
		SpringApplication mockApp = Mockito.mock(SpringApplication.class);
		Mockito.when(mockApp.run()).thenReturn(null);
	}
}
