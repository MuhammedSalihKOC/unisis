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
    @Override
    public boolean canRegisterCourse(Long studentId, Long courseId) {
        int totalCredits = getTotalCredits(studentId);
        Double newCourseCredit = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs bulunamadı"))
                .getCredit();
        return (totalCredits + newCourseCredit) <= 45;
    }

    @Override
    public void registerCourse(Long studentId, Long courseId) {
        if (!canRegisterCourse(studentId, courseId)) {
            throw new RuntimeException("Ders eklenemedi: Kredi limiti aşıldı");
        }
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs bulunamadı"));
        if (studentCourseRepository.existsByStudentAndCourse(student, course)) {
            throw new RuntimeException("Zaten bu derse sahipsin");
        }
        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudent(student);
        studentCourse.setCourse(course);

        studentCourseRepository.save(studentCourse);
    }

    @Override
    public void dropCourse(Long studentId, Long courseId) {
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Ders bulunamadı"));

        StudentCourse studentCourse = studentCourseRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new RuntimeException("Bu ders öğrenci tarafından eklenmedi"));

        studentCourseRepository.delete(studentCourse);
    }

    @Override
    public int getTotalCredits(Long studentId) {
        Integer totalCredits = studentCourseRepository.sumCreditsByStudentId(studentId);
        return totalCredits != null ? totalCredits : 0;
    }
    @Override
    public List<Long> getRegisteredCourseIds(Long studentId) {
        return studentCourseRepository.findCourseIdsByStudentId(studentId);
    }
    @Override
    public List<Course> getRegisteredCourses(Long studentId) {
        return studentCourseRepository.findCoursesByStudentId(studentId);
    }



}
