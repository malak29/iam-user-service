package com.iam.user.service;

import com.iam.user.dto.CreateUserRequest;
import com.iam.user.dto.UpdateUserRequest;
import com.iam.common.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserMappingService {

    private final PasswordEncoder passwordEncoder;

    public Mono<User> buildUserFromRequest(CreateUserRequest request) {
        return Mono.fromCallable(() -> {
            User user = new User();
            updateUserFieldsFromCreateRequest(user, request);
            return user;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<User> updateUserFieldsPartial(User user, UpdateUserRequest request) {
        return Mono.fromCallable(() -> {
            updateFieldIfNotNull(user::setEmail, request.getEmail());
            updateFieldIfNotNull(user::setUsername, request.getUsername());
            updateFieldIfNotNull(user::setName, request.getName());
            updateFieldIfNotNull(user::setOrgId, request.getOrgId());
            updateFieldIfNotNull(user::setDepartmentId, request.getDepartmentId());
            updateFieldIfNotNull(user::setUserTypeId, request.getUserTypeId());
            updateFieldIfNotNull(user::setUserStatusId, request.getUserStatusId());
            updateFieldIfNotNull(user::setAuthTypeId, request.getAuthTypeId());

            updatePasswordIfNotEmpty(user, request.getPassword());
            return user;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    // Private helper methods (unchanged)
    private void updateUserFieldsFromCreateRequest(User user, CreateUserRequest request) {
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setName(request.getName());
        user.setOrgId(request.getOrgId());
        user.setDepartmentId(request.getDepartmentId());
        user.setUserTypeId(request.getUserTypeId());
        user.setUserStatusId(request.getUserStatusId());
        user.setAuthTypeId(request.getAuthTypeId());

        updatePasswordIfNotEmpty(user, request.getPassword());
    }

    private <T> void updateFieldIfNotNull(java.util.function.Consumer<T> setter, T value) {
        if (value != null) {
            setter.accept(value);
        }
    }

    private void updatePasswordIfNotEmpty(User user, String password) {
        if (password != null && !password.isEmpty()) {
            user.setHashedPassword(passwordEncoder.encode(password));
        }
    }
}