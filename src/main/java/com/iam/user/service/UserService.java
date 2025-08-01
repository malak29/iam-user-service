package com.iam.user.service;

import com.iam.common.exception.CustomExceptions;
import com.iam.user.config.Messages;
import com.iam.user.dto.CreateUserRequest;
import com.iam.user.dto.UpdateUserRequest;
import com.iam.user.dto.UserResponse;
import com.iam.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final UserValidationService userValidationService;
    private final UserMappingService userMappingService;

    public Mono<UserResponse> createUser(CreateUserRequest request) {
        log.info("Attempting to create user with email: {}", request.getEmail());

        return userValidationService.validateUserCreation(request)
                .then(userMappingService.buildUserFromRequest(request))
                .flatMap(userRepository::save)
                .map(UserResponse::new)
                .doOnSuccess(userResponse -> log.info("User created successfully with ID: {}", userResponse.getUserId()))
                .onErrorMap(ex -> {
                    log.error("Failed to create user with email: {}. Error: {}", request.getEmail(), ex.getMessage());
                    return ex;
                });
    }

    public Mono<UserResponse> getUserById(UUID userId) {
        log.debug("Fetching user by ID: {}", userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new CustomExceptions.UserNotFoundException(
                        String.format(Messages.USER_NOT_FOUND, userId))))
                .map(UserResponse::new)
                .doOnSuccess(userResponse -> log.debug("User retrieved successfully: {}", userId))
                .onErrorMap(Exception.class, ex -> {
                    if (ex instanceof CustomExceptions.UserNotFoundException) {
                        return ex;
                    }
                    log.error("Error retrieving user by ID: {}. Error: {}", userId, ex.getMessage());
                    return new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
                });
    }

    public Mono<UserResponse> getUserByEmail(String email) {
        log.debug("Fetching user by email: {}", email);

        return userRepository.findByEmail(email)
                .switchIfEmpty(Mono.error(new CustomExceptions.UserNotFoundException(
                        String.format(Messages.USER_NOT_FOUND_EMAIL, email))))
                .map(UserResponse::new)
                .doOnSuccess(userResponse -> log.debug("User retrieved successfully by email: {}", email))
                .onErrorMap(Exception.class, ex -> {
                    if (ex instanceof CustomExceptions.UserNotFoundException) {
                        return ex;
                    }
                    log.error("Error retrieving user by email: {}. Error: {}", email, ex.getMessage());
                    return new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
                });
    }

    public Flux<UserResponse> getUsersByOrganization(Integer orgId) {
        log.debug("Fetching users for organization: {}", orgId);

        return userRepository.findByOrgId(orgId)
                .map(UserResponse::new)
                .doOnComplete(() -> log.debug("Users retrieval completed for organization: {}", orgId))
                .onErrorMap(ex -> {
                    log.error("Error retrieving users for organization: {}. Error: {}", orgId, ex.getMessage());
                    return new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
                });
    }

    public Flux<UserResponse> getUsersByDepartment(Integer departmentId) {
        log.debug("Fetching users for department: {}", departmentId);

        return userRepository.findByDepartmentId(departmentId)
                .map(UserResponse::new)
                .doOnComplete(() -> log.debug("Users retrieval completed for department: {}", departmentId))
                .onErrorMap(ex -> {
                    log.error("Error retrieving users for department: {}. Error: {}", departmentId, ex.getMessage());
                    return new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
                });
    }

    public Mono<UserResponse> updateUser(UUID userId, UpdateUserRequest request) {
        log.info("Attempting to update user: {}", userId);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new CustomExceptions.UserNotFoundException(
                        String.format(Messages.USER_NOT_FOUND, userId))))
                .flatMap(existingUser -> userValidationService.validatePartialUserUpdate(request, existingUser)
                        .then(userMappingService.updateUserFieldsPartial(existingUser, request)))
                .flatMap(userRepository::save)
                .map(UserResponse::new)
                .doOnSuccess(userResponse -> log.info("User updated successfully: {}", userId))
                .onErrorMap(ex -> {
                    if (ex instanceof CustomExceptions.UserNotFoundException ||
                            ex instanceof CustomExceptions.ValidationException ||
                            ex instanceof CustomExceptions.EmailAlreadyExistsException) {
                        return ex;
                    }
                    log.error("Error updating user: {}. Error: {}", userId, ex.getMessage());
                    return new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
                });
    }

    public Mono<Void> deleteUser(UUID userId) {
        log.info("Attempting to delete user: {}", userId);

        return userRepository.existsById(userId)
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new CustomExceptions.UserNotFoundException(
                                String.format(Messages.USER_NOT_FOUND, userId)));
                    }
                    return userRepository.deleteById(userId);
                })
                .doOnSuccess(unused -> log.info("User deleted successfully: {}", userId))
                .onErrorMap(ex -> {
                    if (ex instanceof CustomExceptions.UserNotFoundException) {
                        return ex;
                    }
                    log.error("Error deleting user: {}. Error: {}", userId, ex.getMessage());
                    return new RuntimeException(Messages.INTERNAL_SERVER_ERROR, ex);
                });
    }
}