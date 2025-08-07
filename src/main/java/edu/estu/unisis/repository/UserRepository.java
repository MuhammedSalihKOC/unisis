package edu.estu.unisis.repository;

import edu.estu.unisis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserById(Long id);
    User findBySchoolNumber(String schoolNumber);
    User findByEmail(String email);
    boolean existsByEmail(String email);


}
