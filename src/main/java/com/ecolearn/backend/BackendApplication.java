package com.ecolearn.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		// Auto-fix Cloud Database URLs (Render/Railway/Vercel) for Java JDBC
		String url = System.getenv("SPRING_DATASOURCE_URL");
		
		if (url != null && !url.startsWith("jdbc:")) {
			String fixedUrl = url;
			
			// Convert postgres:// or mysql:// to jdbc:postgresql:// or jdbc:mysql://
			if (url.startsWith("postgres://")) {
				fixedUrl = "jdbc:postgresql://" + url.substring(11);
			} else if (url.startsWith("postgresql://")) {
				fixedUrl = "jdbc:postgresql://" + url.substring(13);
			} else if (url.startsWith("mysql://")) {
				fixedUrl = "jdbc:mysql://" + url.substring(8);
			} else {
				fixedUrl = "jdbc:" + url;
			}

			// Force SSL for PostgreSQL (usually Render), but maybe not for MySQL (Railway)
			if (fixedUrl.contains("postgresql") && !fixedUrl.contains("sslmode=")) {
				fixedUrl += (fixedUrl.contains("?") ? "&" : "?") + "sslmode=require";
			}
			
			System.setProperty("spring.datasource.url", fixedUrl);
			System.out.println("🚀 Cloud Connection Fixed: " + fixedUrl);
		}
		
		SpringApplication.run(BackendApplication.class, args);
	}

}
