package com.example.springappuserservice.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.springappuserservice.model.Role;
import com.example.springappuserservice.model.User;
import com.example.springappuserservice.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new IllegalArgumentException("username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        user.setId(null);
        user.setPassword(hashPassword(user.getPassword()));
        return userRepository.save(user);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public List<User> searchUsers(String name) {
        return userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
                name,
                name,
                name);
    }

    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> updateUser(Long id, User details) {
        return userRepository.findById(id).map(user -> {
            if (details.getEmail() != null) {
                user.setEmail(details.getEmail());
            }
            if (details.getFirstName() != null) {
                user.setFirstName(details.getFirstName());
            }
            if (details.getLastName() != null) {
                user.setLastName(details.getLastName());
            }
            user.setPhone(details.getPhone());
            user.setDateOfBirth(details.getDateOfBirth());
            user.setAddress(details.getAddress());
            if (details.getRole() != null) {
                user.setRole(details.getRole());
            }
            if (details.getIsActive() != null) {
                user.setIsActive(details.getIsActive());
            }
            return userRepository.save(user);
        });
    }

    public Optional<User> updateProfile(Long id, User details) {
        return userRepository.findById(id).map(user -> {
            if (details.getFirstName() != null) {
                user.setFirstName(details.getFirstName());
            }
            if (details.getLastName() != null) {
                user.setLastName(details.getLastName());
            }
            user.setPhone(details.getPhone());
            user.setAddress(details.getAddress());
            user.setDateOfBirth(details.getDateOfBirth());
            return userRepository.save(user);
        });
    }

    public Optional<User> changeRole(Long id, Role role) {
        return userRepository.findById(id).map(user -> {
            user.setRole(role);
            return userRepository.save(user);
        });
    }

    public Optional<User> activateUser(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setIsActive(true);
            return userRepository.save(user);
        });
    }

    public Optional<User> deactivateUser(Long id) {
        return userRepository.findById(id).map(user -> {
            user.setIsActive(false);
            return userRepository.save(user);
        });
    }

    public String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(password.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
