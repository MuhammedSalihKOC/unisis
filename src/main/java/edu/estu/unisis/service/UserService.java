package edu.estu.unisis.service;

import edu.estu.unisis.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    void save(User user);
    boolean existsByEmail(String email);
    User authenticate(String identifier, String password);
}
