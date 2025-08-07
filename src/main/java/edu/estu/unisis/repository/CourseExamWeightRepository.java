package edu.estu.unisis.repository;

import edu.estu.unisis.model.CourseExamWeight;
import edu.estu.unisis.model.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseExamWeightRepository extends JpaRepository<CourseExamWeight, Long> {

    Optional<CourseExamWeight> findByCourseIdAndExamTypeId(Long courseId, Long examTypeId);


}
