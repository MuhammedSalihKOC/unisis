package edu.estu.unisis.repository;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.StudentCourse;
import edu.estu.unisis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    List<StudentCourse> findByStudentId(Long studentId);
    @Query("SELECT sc.course FROM StudentCourse sc WHERE sc.student.id = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") Long studentId);
    @Query("SELECT SUM(c.credit) FROM StudentCourse sc JOIN sc.course c WHERE sc.student.id = :studentId")
    Integer sumCreditsByStudentId(@Param("studentId") Long studentId);
    boolean existsByStudentAndCourse(User student, Course course);
    Optional<StudentCourse> findByStudentAndCourse(User student, Course course);
    @Query("SELECT sc.course.id FROM StudentCourse sc WHERE sc.student.id = :studentId")
    List<Long> findCourseIdsByStudentId(@Param("studentId") Long studentId);
}
