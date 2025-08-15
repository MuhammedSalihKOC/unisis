package edu.estu.unisis.repository;

import edu.estu.unisis.model.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamTypeRepository extends JpaRepository<ExamType, Long> {
    @Query("SELECT cew.examType FROM CourseExamWeight cew WHERE cew.course.id = :courseId ORDER BY cew.examType.name DESC")
    List<ExamType> findExamTypesByCourseId(Long courseId);
}
