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

     @Query("SELECT SUM(r.newPartnersRecruited) FROM Report r")
     Long sumTotalAttendance();

     List<Report> findTop5ByOrderBySubmittedAtDesc();

     @Query("SELECT COUNT(r) FROM Report r WHERE r.submittedAt >= :start")
     Long countReportsSince(@Param("start") LocalDateTime start);
}

