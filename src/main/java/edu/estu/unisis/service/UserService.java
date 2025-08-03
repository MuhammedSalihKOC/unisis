package edu.estu.unisis.service;

import edu.estu.unisis.model.User;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public interface UserService {
    void save(User user);
    boolean existsByEmail(String email);
    User authenticate(String identifier, String password);
    User getById(Long id);
    void updateUser(Long id, User user);
    void deleteById(Long id);
    List<User> getAllStudentsSorted(String sortField, String sortDir);
    List<User> getAllInstructorsSorted(String sortField, String sortDir);
    List<User> getAllAdminsSorted(String sortField, String sortDir);

}
