package edu.estu.unisis.service;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.User;

import java.util.List;
import java.util.Optional;

public interface CourseService {
    Optional<Course> getById(Long id);
    List<Course> getAllCourses();
    List<Course> getCoursesByDepartmentOrderBySemester(Long departmentId);
    List<Course> getCoursesByInstructor(User instructor);
    Course saveCourse(Course course);
    Course updateCourse(Course course);
    void deleteCourse(Long id);




}

