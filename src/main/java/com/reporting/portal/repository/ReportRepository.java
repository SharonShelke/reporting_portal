package com.reporting.portal.repository;


import com.reporting.portal.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {
    long count();

    java.util.List<Report> findByRegion(String region);
    java.util.List<Report> findBySubmittedBy(String submittedBy);
}
