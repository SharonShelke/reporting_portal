package com.reporting.portal.repository;

import com.reporting.portal.entity.ReportBackup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportBackupRepository extends JpaRepository<ReportBackup, Long> {
}
