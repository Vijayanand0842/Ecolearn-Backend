package com.ecolearn.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// Auto-fix Render's connection string for Java JDBC
		String url = System.getenv("SPRING_DATASOURCE_URL");
		if (url != null && !url.startsWith("jdbc:")) {
			String fixedUrl = "jdbc:" + url;
			// Ensure SSL is used for Render's managed database
			if (!fixedUrl.contains("sslmode=")) {
				fixedUrl += (fixedUrl.contains("?") ? "&" : "?") + "sslmode=require";
			}
			System.setProperty("spring.datasource.url", fixedUrl);
			System.out.println("JDBC URL Fixed: " + fixedUrl);
		}
		
		SpringApplication.run(BackendApplication.class, args);
	}

}
