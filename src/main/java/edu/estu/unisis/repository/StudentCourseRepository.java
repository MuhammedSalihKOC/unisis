package edu.estu.unisis.repository;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.StudentCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StudentCourseRepository extends JpaRepository<StudentCourse, Long> {
    List<StudentCourse> findByStudentId(Long studentId);
    @Query("SELECT sc.course FROM StudentCourse sc WHERE sc.student.id = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") Long studentId);


}
