package edu.estu.unisis.repository;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByDepartmentIdOrderBySemesterAsc(Long departmentId);
    List<Course> findByInstructor(User instructor);
}
