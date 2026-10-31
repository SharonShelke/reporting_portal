package com.reporting.portal.repository;
import com.reporting.portal.entity.OutreachReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutreachReportRepository extends JpaRepository<OutreachReport, Long> {
    @Query("SELECT COUNT(r) FROM OutreachReport r WHERE (:email IS NULL OR :email = '' OR LOWER(TRIM(r.submitterEmail)) = LOWER(TRIM(:email)))")
    Long countReports(@Param("email") String email);
}
