package com.aichat.service;

import com.aichat.entity.User;
import com.aichat.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User login(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            logger.warn("Login failed: user not found - {}", username);
            return null;
        }

        User user = userOpt.get();

        // Simple password check (in production, use BCrypt)
        if (!user.getPassword().equals(password)) {
            logger.warn("Login failed: wrong password - {}", username);
            return null;
        }

        logger.info("User logged in successfully: {}", username);
        return user;
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User createUser(String username, String password, String nickname) {
        if (existsByUsername(username)) {
            return null;
        }

        User user = User.builder()
                .username(username)
                .password(password)
                .nickname(nickname != null ? nickname : username)
                .avatar("/default-avatar.png")
                .build();

        user = userRepository.save(user);
        logger.info("Created new user: {}", username);
        return user;
    }
}
