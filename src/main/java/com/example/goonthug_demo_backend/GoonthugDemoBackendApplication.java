package com.example.goonthug_demo_backend;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;


@SpringBootApplication
public class GoonthugDemoBackendApplication implements CommandLineRunner {
	@Value("${server.port}") // Вытаскивает порт из application.properties. Аннотация Beans Factory
	private String serverPort;

	public static void main(String[] args) {
		SpringApplication.run(GoonthugDemoBackendApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		BCryptPasswordEncoder encoder  = new BCryptPasswordEncoder();
		System.out.println(encoder.encode("admin"));

		System.out.println("Swagger path: http://localhost:" + serverPort + "/swagger-ui/index.html");
		System.out.println("Application path: http://localhost:" + serverPort + "/");

	}

}
