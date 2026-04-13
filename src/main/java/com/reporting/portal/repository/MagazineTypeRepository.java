package com.reporting.portal.repository;

import com.reporting.portal.entity.MagazineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MagazineTypeRepository extends JpaRepository<MagazineType, Long> {}
