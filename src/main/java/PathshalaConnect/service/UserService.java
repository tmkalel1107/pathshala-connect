package PathshalaConnect.service;

import PathshalaConnect.entity.User;
import PathshalaConnect.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public User byEmail(String email) {
        if (email == null) return null;
        return userRepository.findByEmail(email.trim().toLowerCase()).orElse(null);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        if (id == null) return null;
        return userRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        if (email == null) return false;
        return userRepository.existsByEmail(email.trim().toLowerCase());
    }

    public User save(User user) {
        if (user.getEmail() != null) {
            user.setEmail(user.getEmail().trim().toLowerCase());
        }
        // If password is not already BCrypt hashed (BCrypt begins with $2a$, $2b$, or $2y$)
        if (user.getPassword() != null && !user.getPassword().startsWith("$2")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User login(String email, String rawPassword) {
        if (email == null || rawPassword == null) return null;
        User user = byEmail(email);
        if (user == null || !user.isActive()) return null;

        // Verify password against BCrypt hash, or fallback to plain text for legacy rows
        if (user.getPassword().startsWith("$2")) {
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user;
            }
        } else if (rawPassword.equals(user.getPassword())) {
            // Upgrade legacy plain-text password to BCrypt
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return user;
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc();
    }

    public void toggleActive(Long userId) {
        User user = getById(userId);
        if (user != null) {
            user.setActive(!user.isActive());
            userRepository.save(user);
        }
    }

    public void deleteUser(Long userId) {
        if (userId != null) {
            userRepository.deleteById(userId);
        }
    }

    @Transactional(readOnly = true)
    public long countTotalUsers() {
        return userRepository.count();
    }

    @Transactional(readOnly = true)
    public long countByRole(String role) {
        return userRepository.countByRole(role);
    }
}