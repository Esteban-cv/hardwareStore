package co.edu.sena.HardwareStore;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class HardwareStoreApplicationTests {

	@Test
	void contextLoads() {
	}

}
