package edu.estu.unisis.service;

import edu.estu.unisis.model.User;
import edu.estu.unisis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Service
public class UserManager implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;}


    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User authenticate(String identifier, String password) {
        User user = null;

        if (identifier.contains("@")) {
            user = userRepository.findByEmail(identifier);
        } else {
            user = userRepository.findBySchoolNumber(identifier);
        }

        if (user != null && user.getPassword().equals(password)) {
            return user;
        }

        return null;
    }

}
