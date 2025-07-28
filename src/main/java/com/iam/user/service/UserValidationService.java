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

@Service
@RequiredArgsConstructor
@Slf4j
public class UserValidationService {

    private final UserRepository userRepository;

    public void validateUserCreation(CreateUserRequest request) {
        validateEmailExists("creation", request.getEmail());
        validateUsernameExists("creation", request.getUsername());
    }

    public void validateUserUpdate(CreateUserRequest request, User existingUser) {
        validateEmailExistsForUpdate("update", request.getEmail(), existingUser.getEmail());
        validateUsernameExistsForUpdate("update", request.getUsername(), existingUser.getUsername());
    }

    public void validatePartialUserUpdate(UpdateUserRequest request, User existingUser) {
        if (request.getEmail() != null) {
            validateEmailExistsForUpdate("partial update", request.getEmail(), existingUser.getEmail());
        }

        if (request.getUsername() != null) {
            validateUsernameExistsForUpdate("partial update", request.getUsername(), existingUser.getUsername());
        }
    }

    // Private helper methods
    private void validateEmailExists(String eventName, String email) {
        if (userRepository.existsByEmail(email)) {
            warnAndThrowEmailException(eventName, email);
        }
    }

    private void validateEmailExistsForUpdate(String eventName, String newEmail, String currentEmail) {
        if (!currentEmail.equals(newEmail) && userRepository.existsByEmail(newEmail)) {
            warnAndThrowEmailException(eventName, newEmail);
        }
    }

    private void validateUsernameExists(String eventName, String username) {
        if (userRepository.existsByUsername(username)) {
            warnAndThrowUsernameException(eventName, username);
        }
    }

    private void validateUsernameExistsForUpdate(String eventName, String newUsername, String currentUsername) {
        if (!currentUsername.equals(newUsername) && userRepository.existsByUsername(newUsername)) {
            warnAndThrowUsernameException(eventName, newUsername);
        }
    }

    private void warnAndThrowEmailException(String eventName, String email) {
        log.warn("Email already exists during {}: {}", eventName, email);
        throw new CustomExceptions.EmailAlreadyExistsException(
                String.format(Messages.EMAIL_ALREADY_EXISTS, email));
    }

    private void warnAndThrowUsernameException(String eventName, String username) {
        log.warn("Username already exists during {}: {}", eventName, username);
        throw new CustomExceptions.ValidationException(
                String.format(Messages.USERNAME_ALREADY_EXISTS, username));
    }
}