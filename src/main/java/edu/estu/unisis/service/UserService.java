package edu.estu.unisis.service;

import edu.estu.unisis.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    User getById(Long id);
    boolean existsByEmail(String email);
    void save(User user);
    User authenticate(String identifier, String password);
}
