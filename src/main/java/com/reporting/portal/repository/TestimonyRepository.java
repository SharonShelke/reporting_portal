package com.reporting.portal.repository;

import com.reporting.portal.entity.Testimony;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TestimonyRepository extends JpaRepository<Testimony, Long> {
    List<Testimony> findByStatus(String status);
    
    List<Testimony> findByStatusAndCategory(String status, String category);
    
    @Query("SELECT t FROM Testimony t WHERE t.status = 'APPROVED' ORDER BY t.likesCount DESC, t.viewsCount DESC")
    List<Testimony> findTrending();
    
    @Query("SELECT t FROM Testimony t WHERE t.status = 'APPROVED' AND t.isFeatured = true AND t.featuredType = :type")
    List<Testimony> findFeatured(String type);

    List<Testimony> findByUserId(Long userId);
}
