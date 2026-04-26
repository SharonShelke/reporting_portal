package com.reporting.portal.repository;

import com.reporting.portal.entity.MagazineOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface MagazineOrderRepository extends JpaRepository<MagazineOrder, Long> {
    List<MagazineOrder> findByZone(String zone);
    List<MagazineOrder> findByOrderedBy(String email);

    @Query("SELECT SUM(o.totalAmount) FROM MagazineOrder o")
    Double sumTotalAmount();
}
