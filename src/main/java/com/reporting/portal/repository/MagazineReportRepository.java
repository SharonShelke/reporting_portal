package com.reporting.portal.repository;
import com.reporting.portal.entity.MagazineReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MagazineReportRepository extends JpaRepository<MagazineReport, Long> {
    @Query("SELECT COUNT(r) FROM MagazineReport r WHERE (:email IS NULL OR :email = '' OR r.submitterEmail = :email)")
    Long countReports(@Param("email") String email);
}
