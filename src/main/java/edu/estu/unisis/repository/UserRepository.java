package edu.estu.unisis.repository;

import edu.estu.unisis.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    User findByEmail(String email);
    User findBySchoolNumber(String email);
}
