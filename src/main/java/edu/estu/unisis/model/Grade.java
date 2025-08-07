package edu.estu.unisis.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_course_id", nullable = false)
    private UserCourse userCourse;

    @ManyToOne
    @JoinColumn(name = "exam_type_id", nullable = false)
    private ExamType examType;

    @Column(nullable = false)
    private Double grade;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public Grade() {
    }

    public Grade(Long id, UserCourse userCourse, ExamType examType, Double grade,
                 LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userCourse = userCourse;
        this.examType = examType;
        this.grade = grade;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UserCourse getUserCourse() {
        return userCourse;
    }

    public void setUserCourse(UserCourse userCourse) {
        this.userCourse = userCourse;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public Double getGrade() {
        return grade;
    }

    public void setGrade(Double grade) {
        this.grade = grade;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
