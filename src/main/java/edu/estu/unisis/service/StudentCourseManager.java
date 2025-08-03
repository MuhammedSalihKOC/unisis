package edu.estu.unisis.service;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.StudentCourse;
import edu.estu.unisis.model.User;
import edu.estu.unisis.repository.CourseRepository;
import edu.estu.unisis.repository.StudentCourseRepository;
import edu.estu.unisis.repository.UserRepository;
import edu.estu.unisis.service.StudentCourseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentCourseManager implements StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public StudentCourseManager(StudentCourseRepository studentCourseRepository,
                                UserRepository userRepository,
                                CourseRepository courseRepository) {
        this.studentCourseRepository = studentCourseRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public List<Course> getCoursesOfStudent(Long studentId) {
        return studentCourseRepository.findByStudentId(studentId)
                .stream()
                .map(StudentCourse::getCourse)
                .toList();
    }

}
