package edu.estu.unisis.repository;

import edu.estu.unisis.model.Exam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    @Query("SELECT COUNT(e) > 0 FROM Exam e WHERE e.course.id = :courseId AND e.examType.id = :examTypeId")
    boolean existsByCourseAndExamType(@Param("courseId") Long courseId, @Param("examTypeId") Long examTypeId);
    Optional<Exam> findFirstByCourseIdAndExamTypeId(Long courseId, Long examTypeId);
    List<Exam> findByCourseId(long courseId);
    List<Exam> findByDepartmentId(long departmentId);
    Page<Exam> findByExamDatetimeAfterOrderByExamDatetimeAsc(LocalDateTime now, Pageable pageable);

    Exam findFirstByExamDatetimeAfterOrderByExamDatetimeAsc(LocalDateTime now);

    List<Exam> findByExamDatetimeBetweenOrderByExamDatetimeAsc(LocalDateTime start, LocalDateTime end);
    List<Exam> findTop10ByExamDatetimeAfterOrderByExamDatetimeAsc(LocalDateTime now);


}
