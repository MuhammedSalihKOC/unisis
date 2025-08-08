package edu.estu.unisis.repository;

import edu.estu.unisis.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    Optional<Grade> findByUserCourseUserIdAndUserCourseCourseIdAndExamTypeId(
            Long userId, Long courseId, Long examTypeId
    );
}
