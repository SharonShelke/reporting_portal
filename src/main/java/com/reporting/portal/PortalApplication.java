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
		SpringApplication.run(PortalApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeDatabase(UserRepository userRepository, DataSource dataSource) {
		return args -> {
			final String expectedDb = "loveworld_reports";
			// Debug: verify which schema the app is actually connected to.
			try (Connection c = dataSource.getConnection();
			     ResultSet rsDb = c.createStatement().executeQuery("SELECT DATABASE()");
			     ResultSet rsUser = c.createStatement().executeQuery("SELECT USER()")) {
				String dbName = rsDb.next() ? rsDb.getString(1) : "(none)";
				String mysqlUser = rsUser.next() ? rsUser.getString(1) : "(none)";
				System.out.println("DB debug - connected database: " + dbName + ", mysql user: " + mysqlUser);

				if (dbName != null && !expectedDb.equalsIgnoreCase(dbName)) {
					throw new RuntimeException(
						"Connected to wrong database '" + dbName + "'. Expected '" + expectedDb +
						"'. Update your Spring datasource URL / environment variables, then restart.");
				}
			} catch (RuntimeException e) {
				// Wrong database detected: stop startup so Hibernate doesn't create/update tables in the wrong schema.
				throw e;
			} catch (Exception e) {
				System.out.println("DB debug - failed to query database/user: " + e.getMessage());
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

			if (!userRepository.existsByEmail("admin@loveworld.com")) {
				User admin2 = new User();
				admin2.setFirstName("Portal");
				admin2.setLastName("Admin");
				admin2.setEmail("admin@loveworld.com");
				admin2.setPassword("Admin123!");
				admin2.setRole("admin");
				admin2.setRegion("Global");
				admin2.setStatus("active");
				userRepository.save(admin2);
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
