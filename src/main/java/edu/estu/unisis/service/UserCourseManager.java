package edu.estu.unisis.service;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.UserCourse;
import edu.estu.unisis.model.User;
import edu.estu.unisis.repository.CourseRepository;
import edu.estu.unisis.repository.UserCourseRepository;
import edu.estu.unisis.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserCourseManager implements UserCourseService {

    private final UserCourseRepository userCourseRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public UserCourseManager(UserCourseRepository userCourseRepository,
                             UserRepository userRepository,
                             CourseRepository courseRepository) {
        this.userCourseRepository = userCourseRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public Optional<UserCourse> getById(Long id) {
        return userCourseRepository.findById((id));
    }

    @Override
    public List<Course> getCoursesOfUser(Long userId) {
        return userCourseRepository.findByUserId(userId)
                .stream()
                .map(UserCourse::getCourse)
                .toList();
    }
    @Override
    public List<User> getUsersOfCourse(Long courseId) {
        return userCourseRepository.findByCourseId(courseId)
                .stream()
                .map(UserCourse::getUser)
                .toList();
    }

    @Override
    public boolean canRegisterCourse(Long userId, Long courseId) {
        int totalCredits = getTotalCredits(userId);
        Double newCourseCredit = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs bulunamadı"))
                .getCredit();
        return (totalCredits + newCourseCredit) <= 45;
    }

    @Override
    public void registerCourse(Long userId, Long courseId) {
        if (!canRegisterCourse(userId, courseId)) {
            throw new RuntimeException("Ders eklenemedi: Kredi limiti aşıldı");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Öğrenci bulunamadı"));

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Kurs bulunamadı"));
        if (userCourseRepository.existsByUserAndCourse(user, course)) {
            throw new RuntimeException("Zaten bu derse sahipsin");
        }
        UserCourse userCourse = new UserCourse();
        userCourse.setUser(user);
        userCourse.setCourse(course);

        userCourseRepository.save(userCourse);
    }

    @Override
    public void dropCourse(Long userId, Long courseId) {
        UserCourse userCourse = userCourseRepository.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new RuntimeException("Bu ders öğrenci tarafından eklenmedi"));

        userCourseRepository.delete(userCourse);
    }

    @Override
    public int getTotalCredits(Long userId) {
        Integer totalCredits = userCourseRepository.sumCreditsByUserId(userId);
        return totalCredits != null ? totalCredits : 0;
    }

    @Override
    public List<Long> getRegisteredCourseIds(Long userId) {
        return userCourseRepository.findCourseIdsByUserId(userId);
    }

    @Override
    public List<Course> getRegisteredCourses(Long userId) {
        return userCourseRepository.findCoursesByUserId(userId);
    }

    public List<UserCourse> getByCourseId(Long courseId) {
        return userCourseRepository.findByCourseIdWithUserAndGrades(courseId);
    }


}
