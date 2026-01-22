package com.staffmanagement.controller;

import com.staffmanagement.model.PerformanceReview;
import com.staffmanagement.service.PerformanceReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/performance")
@CrossOrigin(origins = "http://localhost:3000")
public class PerformanceReviewController {
    
    @Autowired
    private PerformanceReviewService performanceReviewService;
    
    @PostMapping("/review")
    public ResponseEntity<?> createReview(@RequestBody PerformanceReview review) {
        try {
            PerformanceReview savedReview = performanceReviewService.createReview(review);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Performance review created successfully");
            response.put("review", savedReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @PutMapping("/review/{reviewId}")
    public ResponseEntity<?> updateReview(@PathVariable Long reviewId, @RequestBody PerformanceReview reviewDetails) {
        try {
            PerformanceReview updatedReview = performanceReviewService.updateReview(reviewId, reviewDetails);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Performance review updated successfully");
            response.put("review", updatedReview);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{username}")
    public ResponseEntity<?> getUserReviews(@PathVariable String username) {
        try {
            List<PerformanceReview> reviews = performanceReviewService.getUserReviews(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reviews", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/manager/{managerUsername}")
    public ResponseEntity<?> getReviewsByManager(@PathVariable String managerUsername) {
        try {
            List<PerformanceReview> reviews = performanceReviewService.getReviewsByManager(managerUsername);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("reviews", reviews);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{username}/period/{reviewPeriod}")
    public ResponseEntity<?> getReviewByPeriod(@PathVariable String username, @PathVariable String reviewPeriod) {
        try {
            PerformanceReview review = performanceReviewService.getReviewByPeriod(username, reviewPeriod);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("review", review);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/user/{username}/average-rating")
    public ResponseEntity<?> getAverageRating(@PathVariable String username) {
        try {
            Double averageRating = performanceReviewService.getAverageRating(username);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("averageRating", averageRating);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}