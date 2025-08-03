package edu.estu.unisis.service;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.StudentCourse;
import java.util.List;

public interface StudentCourseService {
    List<Course> getCoursesOfStudent(Long studentId);

}
