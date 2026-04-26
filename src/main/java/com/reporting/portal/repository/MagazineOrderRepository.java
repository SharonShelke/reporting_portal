package com.reporting.portal.repository;

import com.reporting.portal.entity.MagazineOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface MagazineOrderRepository extends JpaRepository<MagazineOrder, Long> {
    List<MagazineOrder> findByZone(String zone);
    List<MagazineOrder> findByOrderedBy(String email);

    @Query("SELECT SUM(o.totalAmount) FROM MagazineOrder o WHERE (:email IS NULL OR o.orderedBy = :email)")
    Double sumTotalAmount(@Param("email") String email);

    @Query("SELECT COUNT(o) FROM MagazineOrder o WHERE (:email IS NULL OR o.orderedBy = :email)")
    Long countOrders(@Param("email") String email);
}
