package com.staffmanagement.repository;

import com.staffmanagement.model.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    
    List<PerformanceReview> findByUsernameOrderByReviewDateDesc(String username);
    
    List<PerformanceReview> findByReviewedByOrderByReviewDateDesc(String reviewedBy);
    
    List<PerformanceReview> findByStatusOrderByReviewDateDesc(String status);
    
    @Query("SELECT pr FROM PerformanceReview pr WHERE pr.username = :username AND pr.reviewPeriod = :reviewPeriod")
    Optional<PerformanceReview> findByUsernameAndReviewPeriod(@Param("username") String username, 
                                                            @Param("reviewPeriod") String reviewPeriod);
    
    @Query("SELECT AVG(pr.overallRating) FROM PerformanceReview pr WHERE pr.username = :username")
    Double findAverageRatingByUsername(@Param("username") String username);
}