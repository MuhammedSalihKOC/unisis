package edu.estu.unisis.model;

import jakarta.persistence.*;

@Entity
@Table(name = "course_exam_weights", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"course_id", "exam_type_id"})
})
public class CourseExamWeight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne
    @JoinColumn(name = "exam_type_id", nullable = false)
    private ExamType examType;

    @Column(name = "weight_percentage", nullable = false)
    private Double weightPercentage;

    public CourseExamWeight() {
    }

    public CourseExamWeight(Long id, Course course, ExamType examType, Double weightPercentage) {
        this.id = id;
        this.course = course;
        this.examType = examType;
        this.weightPercentage = weightPercentage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public ExamType getExamType() {
        return examType;
    }

    public void setExamType(ExamType examType) {
        this.examType = examType;
    }

    public Double getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(Double weightPercentage) {
        this.weightPercentage = weightPercentage;
    }

}
