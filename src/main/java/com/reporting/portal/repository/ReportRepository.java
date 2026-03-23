package com.reporting.portal.repository;


import com.reporting.portal.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, String> {
    // Allows backend to auto-increment the custom RPT-XXX ID
    long count();
}
