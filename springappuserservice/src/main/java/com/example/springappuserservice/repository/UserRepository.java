package com.example.springappuserservice.repository;

import java.util.Optional;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.springappuserservice.model.Role;
import com.example.springappuserservice.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(
            String firstName,
            String lastName,
            String username);
}
