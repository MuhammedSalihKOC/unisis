package edu.estu.unisis.service;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.User;
import edu.estu.unisis.model.UserCourse;

import java.util.List;
import java.util.Optional;

public interface UserCourseService {
    Optional<UserCourse> getById(Long id);
    List<UserCourse> getByCourseId(Long courseId);
    List<Course> getCoursesOfUser(Long UserId);
    List<User> getUsersOfCourse(Long courseId);
    List<Course> getRegisteredCourses(Long userId);
    List<Long> getRegisteredCourseIds(Long userId);
    boolean canRegisterCourse(Long userId, Long courseId);
    void registerCourse(Long userId, Long courseId);
    void dropCourse(Long userId, Long courseId);
    int getTotalCredits(Long userId);
}
