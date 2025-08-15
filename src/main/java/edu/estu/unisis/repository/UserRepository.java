package edu.estu.unisis.repository;

import edu.estu.unisis.model.Department;
import edu.estu.unisis.model.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    User findBySchoolNumber(String email);
    @Query("select u from User u where u.role.name = :roleName")
    List<User> findAllByRoleName(@Param("roleName") String roleName, Sort sort);

    void deleteById(Long id);


}
