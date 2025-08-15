package edu.estu.unisis.repository;

import edu.estu.unisis.model.CourseExamWeight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseExamWeightRepository extends JpaRepository<CourseExamWeight, Long> {
    Optional<CourseExamWeight> findByCourseIdAndExamTypeId(Long courseId, Long examTypeId);
    @Query("SELECT COALESCE(SUM(cew.weightPercentage), 0) " +
            "FROM CourseExamWeight cew " +
            "WHERE cew.course.id = :courseId")
    Double sumWeightByCourseId(@Param("courseId") Long courseId);
    List<CourseExamWeight> findByCourseId(Long courseId);
    @Query("""
           select w
           from CourseExamWeight w
           where w.course.id = :courseId
           order by w.examType.name asc
           """)
    List<CourseExamWeight> findByCourseIdOrderByExamTypeName(Long courseId);
}
