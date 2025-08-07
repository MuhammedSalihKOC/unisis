package edu.estu.unisis.repository;

import edu.estu.unisis.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    @Query("SELECT g FROM Grade g WHERE g.userCourse.user.id = :userId AND g.userCourse.course.id = :courseId AND g.examType.id = :examTypeId")
    Optional<Grade> findByUserCourseAndExamType(@Param("userId") Long userId,
                                                @Param("courseId") Long courseId,
                                                @Param("examTypeId") Long examTypeId);



}
