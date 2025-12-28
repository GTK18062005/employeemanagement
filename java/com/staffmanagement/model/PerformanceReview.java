package com.staffmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "performance_reviews")
public class PerformanceReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "review_period", nullable = false)
    private String reviewPeriod; // Q1-2024, Q2-2024, Annual-2024

    @Column(name = "review_date", nullable = false)
    private LocalDate reviewDate;

    @Column(name = "reviewed_by", nullable = false)
    private String reviewedBy;

    @Column(name = "technical_skills_rating")
    private Integer technicalSkillsRating; // 1-5

    @Column(name = "communication_rating")
    private Integer communicationRating; // 1-5

    @Column(name = "teamwork_rating")
    private Integer teamworkRating; // 1-5

    @Column(name = "productivity_rating")
    private Integer productivityRating; // 1-5

    @Column(name = "attendance_rating")
    private Integer attendanceRating; // 1-5

    @Column(name = "overall_rating")
    private Double overallRating;

    @Column(name = "strengths", length = 1000)
    private String strengths;

    @Column(name = "areas_for_improvement", length = 1000)
    private String areasForImprovement;

    @Column(name = "goals", length = 1000)
    private String goals;

    @Column(name = "comments", length = 1000)
    private String comments;

    @Column(name = "recommendations", length = 1000)
    private String recommendations;

    @Column(name = "next_review_date")
    private LocalDate nextReviewDate;

    @Column(name = "status")
    private String status; // DRAFT, COMPLETED

    // Constructors
    public PerformanceReview() {
        this.reviewDate = LocalDate.now();
        this.status = "DRAFT";
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getReviewPeriod() { return reviewPeriod; }
    public void setReviewPeriod(String reviewPeriod) { this.reviewPeriod = reviewPeriod; }

    public LocalDate getReviewDate() { return reviewDate; }
    public void setReviewDate(LocalDate reviewDate) { this.reviewDate = reviewDate; }

    public String getReviewedBy() { return reviewedBy; }
    public void setReviewedBy(String reviewedBy) { this.reviewedBy = reviewedBy; }

    public Integer getTechnicalSkillsRating() { return technicalSkillsRating; }
    public void setTechnicalSkillsRating(Integer technicalSkillsRating) { this.technicalSkillsRating = technicalSkillsRating; }

    public Integer getCommunicationRating() { return communicationRating; }
    public void setCommunicationRating(Integer communicationRating) { this.communicationRating = communicationRating; }

    public Integer getTeamworkRating() { return teamworkRating; }
    public void setTeamworkRating(Integer teamworkRating) { this.teamworkRating = teamworkRating; }

    public Integer getProductivityRating() { return productivityRating; }
    public void setProductivityRating(Integer productivityRating) { this.productivityRating = productivityRating; }

    public Integer getAttendanceRating() { return attendanceRating; }
    public void setAttendanceRating(Integer attendanceRating) { this.attendanceRating = attendanceRating; }

    public Double getOverallRating() { return overallRating; }
    public void setOverallRating(Double overallRating) { this.overallRating = overallRating; }

    public String getStrengths() { return strengths; }
    public void setStrengths(String strengths) { this.strengths = strengths; }

    public String getAreasForImprovement() { return areasForImprovement; }
    public void setAreasForImprovement(String areasForImprovement) { this.areasForImprovement = areasForImprovement; }

    public String getGoals() { return goals; }
    public void setGoals(String goals) { this.goals = goals; }

    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }

    public String getRecommendations() { return recommendations; }
    public void setRecommendations(String recommendations) { this.recommendations = recommendations; }

    public LocalDate getNextReviewDate() { return nextReviewDate; }
    public void setNextReviewDate(LocalDate nextReviewDate) { this.nextReviewDate = nextReviewDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}