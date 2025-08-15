package edu.estu.unisis.repository;

import edu.estu.unisis.model.Course;
import edu.estu.unisis.model.User;
import edu.estu.unisis.model.UserCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserCourseRepository extends JpaRepository<UserCourse, Long> {
    List<UserCourse> findByUserId(Long userId);
    @Query("SELECT uc FROM UserCourse uc " +
            "JOIN FETCH uc.user u " +
            "LEFT JOIN FETCH uc.grades g " +
            "WHERE uc.course.id = :courseId")
    List<UserCourse> findByCourseIdWithUserAndGrades(@Param("courseId") Long courseId);
    @Query("SELECT uc FROM UserCourse uc JOIN FETCH uc.user WHERE uc.course.id = :courseId")
    List<UserCourse> findByCourseId(@Param("courseId") Long courseId);
    Optional<UserCourse> findByUserIdAndCourseId(Long userId, Long courseId);
    boolean existsByUserAndCourse(User user, Course course);
    @Query("SELECT sc.course FROM UserCourse sc WHERE sc.user.id = :userId")
    List<Course> findCoursesByUserId(@Param("userId") Long userId);
    @Query("SELECT sc.course.id FROM UserCourse sc WHERE sc.user.id = :userId")
    List<Long> findCourseIdsByUserId(@Param("userId") Long userId);
    @Query("SELECT SUM(c.credit) FROM UserCourse sc JOIN sc.course c WHERE sc.user.id = :userId")
    Integer sumCreditsByUserId(@Param("userId") Long userId);

}
