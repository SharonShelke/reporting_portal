package com.reporting.portal;

import com.reporting.portal.entity.User;
import com.reporting.portal.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootApplication
public class PortalApplication {

	public static void main(String[] args) {
		System.out.println("==============================================");
		System.out.println("Portal Backend Starting - VERSION: 2.3 (POOL FIXED)");
		System.out.println("==============================================");
		SpringApplication.run(PortalApplication.class, args);
	}

	@Bean
	CommandLineRunner initializeDatabase(UserRepository userRepository, DataSource dataSource) {
		return args -> {
			BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

			// 1. Root admin must always exist with role admin and status active
			var adminOpt = userRepository.findByEmail("admin@loveworld.com");
			if (adminOpt.isEmpty()) {
				User admin = new User();
				admin.setFirstName("System");
				admin.setLastName("Administrator");
				admin.setEmail("admin@loveworld.com");
				admin.setPassword(encoder.encode("admin123"));
				admin.setRole("admin");
				admin.setRegion("Global");
				admin.setStatus("active");
				userRepository.save(admin);
			} else {
				User admin = adminOpt.get();
				if (!"admin".equalsIgnoreCase(admin.getRole()) || !"active".equalsIgnoreCase(admin.getStatus())) {
					admin.setRole("admin");
					admin.setStatus("active");
					userRepository.save(admin);
				}
			}

			// 2. Ensure sharonshelke1@gmail.com is strictly zonal role
			userRepository.findByEmail("sharonshelke1@gmail.com").ifPresent(u -> {
				if (!"zonal".equalsIgnoreCase(u.getRole())) {
					u.setRole("zonal");
					userRepository.save(u);
					System.out.println("Clamped sharonshelke1@gmail.com role to 'zonal'");
				}
			});

			// 3. Database cleanup and data synchronization
			try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
				// Enforce strictly 2 roles (admin and zonal) in the database
				stmt.executeUpdate(
					"UPDATE loveworld_reports.users SET role = 'zonal' WHERE LOWER(email) != 'admin@loveworld.com' AND role != 'admin' AND role != 'zonal'"
				);

				// Attribute existing unassigned or default reports to sharonshelke1@gmail.com if zonal manager is Sharon Shelke
				stmt.executeUpdate(
					"UPDATE loveworld_reports.zone_weekly_reports " +
					"SET submitter_email = 'sharonshelke1@gmail.com', submitted_by = 'Sharon Shelke' " +
					"WHERE (submitter_email IS NULL OR submitter_email = '' OR submitter_email = 'admin@loveworld.com') " +
					"AND LOWER(TRIM(zonal_manager)) = 'sharon shelke'"
				);

				// Ensure submitted_by is never empty or placeholder
				stmt.executeUpdate(
					"UPDATE loveworld_reports.zone_weekly_reports " +
					"SET submitted_by = COALESCE(NULLIF(zonal_manager, ''), submitter_email, 'Zonal User') " +
					"WHERE submitted_by IS NULL OR submitted_by = '' OR submitted_by = 'User' OR submitted_by = '—'"
				);

				// Synchronize user report_count column across all 5 report tables in 1 fast query
				stmt.executeUpdate(
					"UPDATE loveworld_reports.users u " +
					"SET report_count = COALESCE(( " +
					"    SELECT COUNT(*) FROM loveworld_reports.zone_weekly_reports r " +
					"    WHERE LOWER(TRIM(r.submitter_email)) = LOWER(TRIM(u.email)) " +
					"), 0) + COALESCE(( " +
					"    SELECT COUNT(*) FROM loveworld_reports.partnership_reports pr " +
					"    WHERE LOWER(TRIM(pr.submitter_email)) = LOWER(TRIM(u.email)) " +
					"), 0) + COALESCE(( " +
					"    SELECT COUNT(*) FROM loveworld_reports.testimonial_reports tr " +
					"    WHERE LOWER(TRIM(tr.submitter_email)) = LOWER(TRIM(u.email)) " +
					"), 0) + COALESCE(( " +
					"    SELECT COUNT(*) FROM loveworld_reports.magazine_reports mr " +
					"    WHERE LOWER(TRIM(mr.submitter_email)) = LOWER(TRIM(u.email)) " +
					"), 0) + COALESCE(( " +
					"    SELECT COUNT(*) FROM loveworld_reports.outreach_reports orpt " +
					"    WHERE LOWER(TRIM(orpt.submitter_email)) = LOWER(TRIM(u.email)) " +
					"), 0)"
				);

				System.out.println("Database startup data normalization complete.");
			} catch (Exception e) {
				System.err.println("Database initialization warning: " + e.getMessage());
			}
		};
	}

}
