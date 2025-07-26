package com.iam.user.service;

import com.iam.common.exception.CustomExceptions;
import com.iam.user.config.Messages;
import com.iam.user.dto.CreateUserRequest;
import com.iam.user.dto.UserResponse;
import com.iam.user.model.User;
import com.iam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse createUser (CreateUserRequest request) {

        log.info("Attempting to create user with email: {}", request.getEmail());

        try {
            validateUserCreation(request);

            User user = buildUserFromRequest(request);
            User savedUser = userRepository.save(user);

            log.info("User created successfully with ID: {} and email: {}", savedUser.getUserId(), savedUser.getEmail());

            return new UserResponse(savedUser);
        } catch (Exception ex) {
            log.error("Failed to create user with email: {} . Error: {}", request.getEmail(), ex.getMessage());
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById (UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException(
                        "User not found with ID: " + userId
                ));
        return new UserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse  getUserByEmail (String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException(
                        "User not found with email: " + email
                ));
        return new UserResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByOrgId (Integer orgId){
        List<User> users = userRepository.findUsersByOrgId(orgId);
        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByDepartment(Integer departmentId) {
        List<User> users = userRepository.findUsersByDepartmentId(departmentId);
        return users.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    public UserResponse updateUser(UUID userId, CreateUserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException(
                        "User not found with ID: " + userId
                ));

        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setOrgId(request.getOrgId());
        user.setDepartmentId(request.getDepartmentId());
        user.setUserTypeId(request.getUserTypeId());
        user.setUserStatusId(request.getUserStatusId());
        user.setAuthTypeId(request.getAuthTypeId());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        }

        User updatedUser = userRepository.save(user);
        log.info("User successfully updated: {}", userId);

        return new UserResponse(updatedUser);
    }

    public void deleteUser(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomExceptions.UserNotFoundException(
                    "User not found with ID: " + userId);
        }

        userRepository.deleteById(userId);
        log.info("User deleted successfully: {}", userId);
    }

    private void validateUserCreation(CreateUserRequest request) {
        warnAndThrowExceptionForEmail("creation", request.getEmail());

        if (userRepository.existsByUsername(request.getUsername())) {
            warnAndThrowExceptionForUsername("creation", request.getUsername());
        }
    }

    private void validateUserUpdate(CreateUserRequest request, User existingUser) {
        warnAndThrowExceptionForEmail("update", request.getEmail());

        if (!existingUser.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
            warnAndThrowExceptionForUsername("update", request.getUsername());
        }
    }

    private void warnAndThrowExceptionForEmail (String eventName, String email) {
        if (userRepository.existsByEmail(email)) {
            log.warn("Email already exist during {}: {}", eventName, email);
            throw new CustomExceptions.EmailAlreadyExistsException(
                    String.format(Messages.EMAIL_ALREADY_EXISTS, email)
            );
        }
    }

    private void warnAndThrowExceptionForUsername (String eventName, String username) {
        log.warn("Username already exists during {}: {}", eventName, username);
        throw new CustomExceptions.ValidationException(
                String.format(Messages.USERNAME_ALREADY_EXISTS, username)
        );
    }

    private User buildUserFromRequest(CreateUserRequest request) {
        User user = new User();
        updateUserFieldsFromRequest(user, request);
        return user;
    }

    private void updateUserFieldsFromRequest(User user, CreateUserRequest request) {
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setOrgId(request.getOrgId());
        user.setDepartmentId(request.getDepartmentId());
        user.setUserTypeId(request.getUserTypeId());
        user.setUserStatusId(request.getUserStatusId());
        user.setAuthTypeId(request.getAuthTypeId());

        if (request.getPassword() != null && !request.getPassword().isEmpty()) {
            user.setHashedPassword(passwordEncoder.encode(request.getPassword()));
        }
    }

}
