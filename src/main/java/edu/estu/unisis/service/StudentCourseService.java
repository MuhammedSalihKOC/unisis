package edu.estu.unisis.service;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.StudentCourse;
import java.util.List;

public interface StudentCourseService {
    List<Course> getCoursesOfStudent(Long studentId);
    boolean canRegisterCourse(Long studentId, Long courseId);
    void registerCourse(Long studentId, Long courseId);
    void dropCourse(Long studentId, Long courseId);
    int getTotalCredits(Long studentId);
    List<Long> getRegisteredCourseIds(Long studentId);
    List<Course> getRegisteredCourses(Long studentId);
    
}
