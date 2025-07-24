package co.edu.sena.HardwareStore;

import org.springframework.boot.SpringApplication;

public class TestHardwareStoreApplication {

	public static void main(String[] args) {
		SpringApplication.from(HardwareStoreApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
