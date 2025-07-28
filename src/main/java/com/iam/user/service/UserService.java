package com.iam.user.service;

import com.iam.common.exception.CustomExceptions;
import com.iam.user.config.Messages;
import com.iam.user.dto.CreateUserRequest;
import com.iam.user.dto.UpdateUserRequest;
import com.iam.user.dto.UserResponse;
import com.iam.user.model.User;
import com.iam.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserValidationService userValidationService;
    private final UserMappingService userMappingService;

    public UserResponse createUser(CreateUserRequest request) {
        log.info("Attempting to create user with email: {}", request.getEmail());

        try {
            userValidationService.validateUserCreation(request);

            User user = userMappingService.buildUserFromRequest(request);
            User savedUser = userRepository.save(user);

            log.info("User created successfully with ID: {} and email: {}",
                    savedUser.getUserId(), savedUser.getEmail());

            return new UserResponse(savedUser);

        } catch (Exception ex) {
            log.error("Failed to create user with email: {}. Error: {}",
                    request.getEmail(), ex.getMessage());
            throw ex;
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(UUID userId) {
        log.debug("Fetching user by ID: {}", userId);

        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found with ID: {}", userId);
                        return new CustomExceptions.UserNotFoundException(
                                String.format(Messages.USER_NOT_FOUND, userId));
                    });

            log.debug("User retrieved successfully: {}", userId);
            return new UserResponse(user);

        } catch (CustomExceptions.UserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error retrieving user by ID: {}. Error: {}", userId, ex.getMessage());
            throw new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.warn("User not found with email: {}", email);
                        return new CustomExceptions.UserNotFoundException(
                                String.format(Messages.USER_NOT_FOUND_EMAIL, email));
                    });

            log.debug("User retrieved successfully by email: {}", email);
            return new UserResponse(user);

        } catch (CustomExceptions.UserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error retrieving user by email: {}. Error: {}", email, ex.getMessage());
            throw new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByOrganization(Integer orgId) {
        log.debug("Fetching users for organization: {}", orgId);

        try {
            List<User> users = userRepository.findUsersByOrgId(orgId);
            log.debug("Found {} users for organization: {}", users.size(), orgId);

            return users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error retrieving users for organization: {}. Error: {}", orgId, ex.getMessage());
            throw new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
        }
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByDepartment(Integer departmentId) {
        log.debug("Fetching users for department: {}", departmentId);

        try {
            List<User> users = userRepository.findUsersByDepartmentId(departmentId);
            log.debug("Found {} users for department: {}", users.size(), departmentId);

            return users.stream()
                    .map(UserResponse::new)
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            log.error("Error retrieving users for department: {}. Error: {}", departmentId, ex.getMessage());
            throw new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
        }
    }

    public UserResponse updateUser(UUID userId, UpdateUserRequest request) {
        log.info("Attempting to update user with partial data: {}", userId);

        try {
            User existingUser = userRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("Cannot update - user not found: {}", userId);
                        return new CustomExceptions.UserNotFoundException(
                                String.format(Messages.USER_NOT_FOUND, userId));
                    });

            userValidationService.validatePartialUserUpdate(request, existingUser);
            userMappingService.updateUserFieldsPartial(existingUser, request);

            User updatedUser = userRepository.save(existingUser);
            log.info("User updated successfully with partial data: {}", userId);

            return new UserResponse(updatedUser);

        } catch (CustomExceptions.UserNotFoundException | CustomExceptions.ValidationException |
                 CustomExceptions.EmailAlreadyExistsException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error updating user: {}. Error: {}", userId, ex.getMessage());
            throw new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
        }
    }

    public void deleteUser(UUID userId) {
        log.info("Attempting to delete user: {}", userId);

        try {
            if (!userRepository.existsById(userId)) {
                log.warn("Cannot delete - user not found: {}", userId);
                throw new CustomExceptions.UserNotFoundException(
                        String.format(Messages.USER_NOT_FOUND, userId));
            }

            userRepository.deleteById(userId);
            log.info("User deleted successfully: {}", userId);

        } catch (CustomExceptions.UserNotFoundException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Error deleting user: {}. Error: {}", userId, ex.getMessage());
            throw new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
        }
    }
}