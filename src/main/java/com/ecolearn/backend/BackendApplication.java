package com.ecolearn.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// Auto-fix Render's connection string if it's missing the 'jdbc:' prefix
		String url = System.getenv("SPRING_DATASOURCE_URL");
		if (url != null && !url.startsWith("jdbc:")) {
			System.setProperty("spring.datasource.url", "jdbc:" + url);
		}
		
		SpringApplication.run(BackendApplication.class, args);
	}

}
