package com.reporting.portal;

import com.reporting.portal.entity.User;
import com.reporting.portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;

@SpringBootApplication
public class PortalApplication {

	public static void main(String[] args) {
		System.out.println("==============================================");
		System.out.println("Portal Backend Starting - VERSION: 2.1 (FIXED)");
		System.out.println("==============================================");
		SpringApplication.run(PortalApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeDatabase(UserRepository userRepository, DataSource dataSource) {
		return args -> {


			if (!userRepository.existsByEmail("sharonshelke1@gmail.com")) {
				User admin = new User();
				admin.setFirstName("Sharon");
				admin.setLastName("Shelke");
				admin.setEmail("sharonshelke1@gmail.com");
				admin.setPassword("admin123");
				admin.setRole("admin");
				admin.setRegion("Global");
				admin.setStatus("active");
				userRepository.save(admin);
			}

			if (!userRepository.existsByEmail("admin@loveworld.com")) {
				User admin = new User();
				admin.setFirstName("System");
				admin.setLastName("Administrator");
				admin.setEmail("admin@loveworld.com");
				admin.setPassword("admin123");
				admin.setRole("admin");
				admin.setRegion("Global");
				admin.setStatus("active");
				userRepository.save(admin);
			}

			if (!userRepository.existsByEmail("global@loveworld.com")) {
				User global = new User();
				global.setFirstName("Global");
				global.setLastName("Partnership Manager");
				global.setEmail("global@loveworld.com");
				global.setPassword("global123");
				global.setRole("global");
				global.setRegion("Global");
				global.setStatus("active");
				userRepository.save(global);
			}

			if (!userRepository.existsByEmail("zonal@loveworld.com")) {
				User zonal = new User();
				zonal.setFirstName("Zonal");
				zonal.setLastName("Partnership Manager");
				zonal.setEmail("zonal@loveworld.com");
				zonal.setPassword("zonal123");
				zonal.setRole("zonal");
				zonal.setRegion("North America");
				zonal.setStatus("active");
				userRepository.save(zonal);
			}


		};
	}

}
