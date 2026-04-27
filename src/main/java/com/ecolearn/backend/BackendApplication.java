package com.ecolearn.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// Auto-fix Render's connection string for Java JDBC
		String url = System.getenv("SPRING_DATASOURCE_URL");
		if (url != null && !url.startsWith("jdbc:")) {
			url = "jdbc:" + url;
			// Ensure SSL is used for Render's managed database
			if (!url.contains("sslmode=")) {
				url += (url.contains("?") ? "&" : "?") + "sslmode=require";
			}
			System.setProperty("spring.datasource.url", url);
		}
		
		SpringApplication.run(BackendApplication.class, args);
	}

}
