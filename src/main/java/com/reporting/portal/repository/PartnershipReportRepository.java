package com.reporting.portal.repository;
import com.reporting.portal.entity.PartnershipReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PartnershipReportRepository extends JpaRepository<PartnershipReport, Long> {
    @Query("SELECT COUNT(r) FROM PartnershipReport r WHERE (:email IS NULL OR :email = '' OR LOWER(r.submitterEmail) = LOWER(:email))")
    Long countReports(@Param("email") String email);
}
