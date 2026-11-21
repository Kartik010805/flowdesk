
package com.example.demo.service;

import com.example.demo.entity.Department;
import com.example.demo.entity.User;

import com.example.demo.enums.EmploymentStatus;
import com.example.demo.enums.Role;

import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.UserRepository;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(
            UserRepository userRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public User createUser(
            String fullName,
            String email,
            String rawPassword,
            Role role,
            Long departmentId,
            Long managerId) {

        if (role == null ||
                (role != Role.EMPLOYEE && role != Role.MANAGER)) {
            throw new IllegalArgumentException(
                    "Only EMPLOYEE and MANAGER accounts can be created here."
            );
        }

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (rawPassword == null || rawPassword.length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters."
            );
        }

        if (userRepository.findByEmail(normalizedEmail).isPresent()) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        if (role == Role.EMPLOYEE && managerId == null) {
            throw new IllegalArgumentException(
                    "An active manager must be assigned to every employee."
            );
        }

        if (role == Role.MANAGER && managerId != null) {
            throw new IllegalArgumentException(
                    "Managers cannot be assigned to another manager."
            );
        }

        User user = new User();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setStatus(EmploymentStatus.ACTIVE);

        if (departmentId != null) {
            Department department = departmentRepository
                    .findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Department not found."
                    ));

            user.setDepartment(department);
        }

        if (role == Role.EMPLOYEE) {
            User manager = getActiveManager(managerId, null);
            user.setManager(manager);
        }

        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(
            Long userId,
            String fullName,
            String email,
            Long departmentId,
            Long managerId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        validateManagedUser(user);

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("Full name is required.");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        String normalizedEmail = email.trim().toLowerCase();

        userRepository.findByEmail(normalizedEmail)
                .filter(existing -> !existing.getId().equals(userId))
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "An account with this email already exists."
                    );
                });

        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);

        if (departmentId == null) {
            user.setDepartment(null);
        } else {
            Department department = departmentRepository
                    .findById(departmentId)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Department not found."
                    ));

            user.setDepartment(department);
        }

        if (user.getRole() == Role.EMPLOYEE) {

            if (managerId == null) {
                throw new IllegalArgumentException(
                        "An active manager must be assigned to every employee."
                );
            }

            User manager = getActiveManager(managerId, userId);
            user.setManager(manager);

        } else if (managerId != null) {

            throw new IllegalArgumentException(
                    "Managers cannot be assigned to another manager."
            );
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deactivateUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        validateManagedUser(user);

        user.setStatus(EmploymentStatus.INACTIVE);
        userRepository.save(user);
    }

    @Transactional
    public void activateUser(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        validateManagedUser(user);

        if (user.getRole() == Role.EMPLOYEE) {

            if (user.getManager() == null ||
                    user.getManager().getStatus() != EmploymentStatus.ACTIVE) {
                throw new IllegalArgumentException(
                        "Assign an active manager before activating this employee."
                );
            }
        }

        user.setStatus(EmploymentStatus.ACTIVE);
        userRepository.save(user);
    }

    private User getActiveManager(Long managerId, Long excludedUserId) {

        User manager = userRepository.findById(managerId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Manager not found."));

        if (excludedUserId != null &&
                manager.getId().equals(excludedUserId)) {
            throw new IllegalArgumentException(
                    "A user cannot be their own manager."
            );
        }

        if (manager.getRole() != Role.MANAGER ||
                manager.getStatus() != EmploymentStatus.ACTIVE) {
            throw new IllegalArgumentException(
                    "Selected user is not an active manager."
            );
        }

        return manager;
    }

    private void validateManagedUser(User user) {

        if (user.getRole() != Role.EMPLOYEE &&
                user.getRole() != Role.MANAGER) {
            throw new IllegalArgumentException(
                    "ADMIN accounts cannot be modified here."
            );
        }
    }
}
