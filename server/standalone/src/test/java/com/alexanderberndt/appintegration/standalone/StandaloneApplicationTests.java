package com.alexanderberndt.appintegration.standalone;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class StandaloneApplicationTests {

	@Autowired
	StandaloneApplication standaloneApplication;

	@Test
	void contextLoads() {
		assertNotNull(standaloneApplication);

	}

}
