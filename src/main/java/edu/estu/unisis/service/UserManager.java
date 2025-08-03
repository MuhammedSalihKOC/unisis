package edu.estu.unisis.service;


import edu.estu.unisis.model.User;
import edu.estu.unisis.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserManager implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public UserManager(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }


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
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }

        return null;
    }
    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElse(null); // veya .orElseThrow()
    }
    @Override
    public void updateUser(Long id, User updatedUser) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));
        existingUser.setName(updatedUser.getName());
        existingUser.setEmail(updatedUser.getEmail());
        existingUser.setSchoolNumber(updatedUser.getSchoolNumber());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
            String hashedPassword = passwordEncoder.encode(updatedUser.getPassword());
            existingUser.setPassword(hashedPassword);
        }
        existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        existingUser.setDepartment(updatedUser.getDepartment());
        if (updatedUser.getReceipt() != null) {
            existingUser.setReceipt(updatedUser.getReceipt());
        }
        userRepository.save(existingUser);
    }
    @Override
    public List<User> getAllStudentsSorted(String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        return userRepository.findAllStudentsSorted(sort);
    }
    @Override
    public List<User> getAllInstructorsSorted(String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        return userRepository.findAllInstructorsSorted(sort);
    }
    @Override
    public List<User> getAllAdminsSorted(String sortField, String sortDir) {
        Sort sort = Sort.by(sortField);
        sort = sortDir.equalsIgnoreCase("asc") ? sort.ascending() : sort.descending();
        return userRepository.findAllAdminsSorted(sort);
    }
    @Override
    public void deleteById(Long id){
        boolean exists = userRepository.existsById(id);
        System.out.println("Silmeden önce var mı? " + exists);
        userRepository.deleteById(id);
    }
}
