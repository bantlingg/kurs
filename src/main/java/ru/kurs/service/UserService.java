package ru.kurs.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.kurs.entity.User;
import ru.kurs.entity.Role;
import ru.kurs.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
    }

    public User createUser(User user) {
        log.info("Creating user with role: {}", user.getRole());
        validateUser(user);
        try {
            log.info("User details before save - username: {}, email: {}, role: {}",
                    user.getUserName(), user.getEmail(), user.getRole());
            User savedUser = userRepository.save(user);
            log.info("Successfully created user with role: {}", savedUser.getRole());
            return savedUser;
        } catch (Exception e) {
            log.error("Error creating user: {}", e.getMessage(), e);
            throw e;
        }
    }

    public User updateUser(User user) {
        validateUser(user);
        if (!userRepository.existsById(user.getUserId())) {
            throw new IllegalArgumentException("User not found with id: " + user.getUserId());
        }
        return userRepository.save(user);
    }

    private void validateUser(User user) {
        if (user.getRole() == Role.MANAGER || user.getRole() == Role.USER || user.getRole() == Role.BLASTING_ENGINEER) {
            if (user.getQuarry() == null) {
                throw new IllegalArgumentException(
                        "User with role " + user.getRole() + " must be associated with a quarry");
            }
        }
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}