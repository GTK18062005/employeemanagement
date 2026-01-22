package com.staffmanagement.service;

import com.staffmanagement.model.PerformanceReview;
import com.staffmanagement.repository.PerformanceReviewRepository;
import com.staffmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PerformanceReviewService {
    
    @Autowired
    private PerformanceReviewRepository performanceReviewRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    public PerformanceReview createReview(PerformanceReview review) {
        // Validate that reviewer exists and is a manager
        if (!isValidReviewer(review.getReviewedBy())) {
            throw new RuntimeException("Reviewer must be a manager or project manager");
        }
        
        // Calculate overall rating
        calculateOverallRating(review);
        
        return performanceReviewRepository.save(review);
    }
    
    public PerformanceReview updateReview(Long reviewId, PerformanceReview reviewDetails) {
        Optional<PerformanceReview> reviewOpt = performanceReviewRepository.findById(reviewId);
        if (reviewOpt.isPresent()) {
            PerformanceReview review = reviewOpt.get();
            
            // Update fields
            if (reviewDetails.getTechnicalSkillsRating() != null) {
                review.setTechnicalSkillsRating(reviewDetails.getTechnicalSkillsRating());
            }
            if (reviewDetails.getCommunicationRating() != null) {
                review.setCommunicationRating(reviewDetails.getCommunicationRating());
            }
            if (reviewDetails.getTeamworkRating() != null) {
                review.setTeamworkRating(reviewDetails.getTeamworkRating());
            }
            if (reviewDetails.getProductivityRating() != null) {
                review.setProductivityRating(reviewDetails.getProductivityRating());
            }
            if (reviewDetails.getAttendanceRating() != null) {
                review.setAttendanceRating(reviewDetails.getAttendanceRating());
            }
            if (reviewDetails.getStrengths() != null) {
                review.setStrengths(reviewDetails.getStrengths());
            }
            if (reviewDetails.getAreasForImprovement() != null) {
                review.setAreasForImprovement(reviewDetails.getAreasForImprovement());
            }
            if (reviewDetails.getGoals() != null) {
                review.setGoals(reviewDetails.getGoals());
            }
            if (reviewDetails.getComments() != null) {
                review.setComments(reviewDetails.getComments());
            }
            if (reviewDetails.getRecommendations() != null) {
                review.setRecommendations(reviewDetails.getRecommendations());
            }
            if (reviewDetails.getNextReviewDate() != null) {
                review.setNextReviewDate(reviewDetails.getNextReviewDate());
            }
            if (reviewDetails.getStatus() != null) {
                review.setStatus(reviewDetails.getStatus());
            }
            
            // Recalculate overall rating
            calculateOverallRating(review);
            
            return performanceReviewRepository.save(review);
        }
        throw new RuntimeException("Performance review not found");
    }
    
    public List<PerformanceReview> getUserReviews(String username) {
        return performanceReviewRepository.findByUsernameOrderByReviewDateDesc(username);
    }
    
    public List<PerformanceReview> getReviewsByManager(String managerUsername) {
        return performanceReviewRepository.findByReviewedByOrderByReviewDateDesc(managerUsername);
    }
    
    public PerformanceReview getReviewByPeriod(String username, String reviewPeriod) {
        return performanceReviewRepository.findByUsernameAndReviewPeriod(username, reviewPeriod)
                .orElse(null);
    }
    
    public Double getAverageRating(String username) {
        Double avg = performanceReviewRepository.findAverageRatingByUsername(username);
        return avg != null ? avg : 0.0;
    }
    
    private void calculateOverallRating(PerformanceReview review) {
        int count = 0;
        double total = 0;
        
        if (review.getTechnicalSkillsRating() != null) {
            total += review.getTechnicalSkillsRating();
            count++;
        }
        if (review.getCommunicationRating() != null) {
            total += review.getCommunicationRating();
            count++;
        }
        if (review.getTeamworkRating() != null) {
            total += review.getTeamworkRating();
            count++;
        }
        if (review.getProductivityRating() != null) {
            total += review.getProductivityRating();
            count++;
        }
        if (review.getAttendanceRating() != null) {
            total += review.getAttendanceRating();
            count++;
        }
        
        if (count > 0) {
            review.setOverallRating(Math.round((total / count) * 100.0) / 100.0);
        }
    }
    
    private boolean isValidReviewer(String username) {
        return userRepository.findByUsername(username)
                .map(user -> "PROJECT_MANAGER".equals(user.getRole()) || "ADMIN".equals(user.getRole()))
                .orElse(false);
    }
}