package com.reporting.portal.repository;


import com.reporting.portal.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

     List<Report> findBySubmitterEmail(String submitterEmail);
    }

