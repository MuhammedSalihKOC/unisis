package edu.estu.unisis.repository;

import edu.estu.unisis.model.Department;
import edu.estu.unisis.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    User findBySchoolNumber(String email);
    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'STUDENT'")
    List<User> findAllStudentsSorted(Sort sort);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'INSTRUCTOR'")
    List<User> findAllInstructorsSorted(Sort sort);

    @Query("SELECT u FROM User u JOIN u.roles r WHERE r.name = 'ADMIN'")
    List<User> findAllAdminsSorted(Sort sort);

    void deleteById(Long id);


}
