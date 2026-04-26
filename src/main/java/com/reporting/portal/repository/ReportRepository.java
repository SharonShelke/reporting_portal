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

     @Query("SELECT SUM(r.newPartnersRecruited) FROM Report r WHERE (:email IS NULL OR r.submitterEmail = :email)")
     Long sumTotalAttendance(@Param("email") String email);

     List<Report> findTop5ByOrderBySubmittedAtDesc();

     List<Report> findTop5BySubmitterEmailOrderBySubmittedAtDesc(String email);

     @Query("SELECT COUNT(r) FROM Report r WHERE r.submittedAt >= :start AND (:email IS NULL OR r.submitterEmail = :email)")
     Long countReportsSince(@Param("start") LocalDateTime start, @Param("email") String email);

     @Query("SELECT COUNT(r) FROM Report r WHERE (:email IS NULL OR r.submitterEmail = :email)")
     Long countReports(@Param("email") String email);
}
