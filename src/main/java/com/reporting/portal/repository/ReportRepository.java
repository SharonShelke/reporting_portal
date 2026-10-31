package com.reporting.portal.repository;


import com.reporting.portal.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

     List<Report> findBySubmitterEmail(String submitterEmail);
     List<Report> findByRegionName(String regionName);

     List<Report> findTop5ByOrderBySubmittedAtDesc();
     List<Report> findTop5BySubmitterEmailOrderBySubmittedAtDesc(String email);

     @Query("SELECT r FROM Report r WHERE (:email IS NULL OR :email = '' OR LOWER(TRIM(r.submitterEmail)) = LOWER(TRIM(:email)) OR LOWER(TRIM(r.submittedBy)) = LOWER(TRIM(:email))) ORDER BY r.id DESC")
     List<Report> findByEmailOrSubmitter(@Param("email") String email);

     @Query("SELECT COUNT(r) FROM Report r WHERE (:email IS NULL OR :email = '' OR LOWER(TRIM(r.submitterEmail)) = LOWER(TRIM(:email)) OR LOWER(TRIM(r.submittedBy)) = LOWER(TRIM(:email)))")
     Long countReports(@Param("email") String email);

     @Query("SELECT COUNT(r) FROM Report r WHERE r.submittedAt >= :start AND (:email IS NULL OR :email = '' OR LOWER(TRIM(r.submitterEmail)) = LOWER(TRIM(:email)) OR LOWER(TRIM(r.submittedBy)) = LOWER(TRIM(:email)))")
     Long countReportsSince(@Param("start") LocalDateTime start, @Param("email") String email);

     @Query("SELECT SUM(r.totalRegistrationHslhs) FROM Report r WHERE (:email IS NULL OR :email = '' OR LOWER(TRIM(r.submitterEmail)) = LOWER(TRIM(:email)) OR LOWER(TRIM(r.submittedBy)) = LOWER(TRIM(:email)))")
     Long sumTotalAttendance(@Param("email") String email);
}
