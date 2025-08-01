package com.iam.user.service;

import com.iam.common.exception.CustomExceptions;
import com.iam.user.config.Messages;
import com.iam.user.dto.CreateUserRequest;
import com.iam.user.dto.UpdateUserRequest;
import com.iam.common.model.User;
import com.iam.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final UserRepository userRepository;

    public Mono<Void> validateUserCreation(CreateUserRequest request) {
        return validateEmailExists("creation", request.getEmail())
                .then(validateUsernameExists("creation", request.getUsername()));
    }

    public Mono<Void> validateUserUpdate(CreateUserRequest request, User existingUser) {
        return validateEmailExistsForUpdate("update", request.getEmail(), existingUser.getEmail())
                .then(validateUsernameExistsForUpdate("update", request.getUsername(), existingUser.getUsername()));
    }

    public Mono<Void> validatePartialUserUpdate(UpdateUserRequest request, User existingUser) {
        Mono<Void> emailValidation = request.getEmail() != null ?
                validateEmailExistsForUpdate("partial update", request.getEmail(), existingUser.getEmail()) :
                Mono.empty();

        Mono<Void> usernameValidation = request.getUsername() != null ?
                validateUsernameExistsForUpdate("partial update", request.getUsername(), existingUser.getUsername()) :
                Mono.empty();

        return emailValidation.then(usernameValidation);
    }

    // Private helper methods
    private Mono<Void> validateEmailExists(String eventName, String email) {
        return userRepository.existsByEmail(email)
                .flatMap(exists -> {
                    if (exists) {
                        return warnAndThrowEmailException(eventName, email);
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateEmailExistsForUpdate(String eventName, String newEmail, String currentEmail) {
        if (currentEmail.equals(newEmail)) {
            return Mono.empty();
        }

        return userRepository.existsByEmail(newEmail)
                .flatMap(exists -> {
                    if (exists) {
                        return warnAndThrowEmailException(eventName, newEmail);
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateUsernameExists(String eventName, String username) {
        return userRepository.existsByUsername(username)
                .flatMap(exists -> {
                    if (exists) {
                        return warnAndThrowUsernameException(eventName, username);
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validateUsernameExistsForUpdate(String eventName, String newUsername, String currentUsername) {
        if (currentUsername.equals(newUsername)) {
            return Mono.empty();
        }

        return userRepository.existsByUsername(newUsername)
                .flatMap(exists -> {
                    if (exists) {
                        return warnAndThrowUsernameException(eventName, newUsername);
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> warnAndThrowEmailException(String eventName, String email) {
        log.warn("Email already exists during {}: {}", eventName, email);
        return Mono.error(new CustomExceptions.EmailAlreadyExistsException(
                String.format(Messages.EMAIL_ALREADY_EXISTS, email)));
    }

    private Mono<Void> warnAndThrowUsernameException(String eventName, String username) {
        log.warn("Username already exists during {}: {}", eventName, username);
        return Mono.error(new CustomExceptions.ValidationException(
                String.format(Messages.USERNAME_ALREADY_EXISTS, username)));
    }
}