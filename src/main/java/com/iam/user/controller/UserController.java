package com.iam.user.controller;

import com.iam.common.response.ApiResponse;
import com.iam.user.config.ApiRoutes;
import com.iam.user.config.Messages;
import com.iam.user.dto.CreateUserRequest;
import com.iam.user.dto.UpdateUserRequest;
import com.iam.user.dto.UserResponse;
import com.iam.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.USERS)
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> createUser(@Valid @RequestBody Mono<CreateUserRequest> requestMono) {
        return requestMono
                .doOnNext(request -> log.info("Received request to create user with email: {}", request.getEmail()))
                .flatMap(userService::createUser)
                .map(userResponse -> ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.success(userResponse, Messages.USER_CREATED_SUCCESS)))
                .doOnSuccess(response -> log.info("User creation completed successfully"));
    }

    @GetMapping(ApiRoutes.USER_BY_ID)
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getUserById(@PathVariable UUID userId) {
        log.debug("Received request to get user by ID: {}", userId);

        return userService.getUserById(userId)
                .map(userResponse -> ResponseEntity.ok(ApiResponse.success(userResponse, Messages.USER_RETRIEVED_SUCCESS)))
                .doOnSuccess(response -> log.debug("User retrieval by ID completed successfully: {}", userId));
    }

    @GetMapping(ApiRoutes.USER_BY_EMAIL)
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> getUserByEmail(@PathVariable String email) {
        log.debug("Received request to get user by email: {}", email);

        return userService.getUserByEmail(email)
                .map(userResponse -> ResponseEntity.ok(ApiResponse.success(userResponse, Messages.USER_RETRIEVED_SUCCESS)))
                .doOnSuccess(response -> log.debug("User retrieval by email completed successfully: {}", email));
    }

    @GetMapping(ApiRoutes.USERS_BY_ORGANIZATION)
    public Mono<ResponseEntity<ApiResponse<List<UserResponse>>>> getUsersByOrganization(@PathVariable Integer orgId) {
        log.debug("Received request to get users by organization: {}", orgId);

        return userService.getUsersByOrganization(orgId)
                .collectList()
                .map(users -> ResponseEntity.ok(ApiResponse.success(users, Messages.USERS_RETRIEVED_SUCCESS)))
                .doOnSuccess(response -> log.debug("Users retrieval by organization completed for org: {}", orgId));
    }

    @PutMapping(ApiRoutes.USER_BY_ID)
    public Mono<ResponseEntity<ApiResponse<UserResponse>>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody Mono<UpdateUserRequest> requestMono) {

        return requestMono
                .doOnNext(request -> log.info("Received request to update user: {}", userId))
                .flatMap(request -> userService.updateUser(userId, request))
                .map(userResponse -> ResponseEntity.ok(ApiResponse.success(userResponse, Messages.USER_UPDATED_SUCCESS)))
                .doOnSuccess(response -> log.info("User update completed successfully: {}", userId));
    }

    @DeleteMapping(ApiRoutes.USER_BY_ID)
    public Mono<ResponseEntity<ApiResponse<Void>>> deleteUser(@PathVariable UUID userId) {
        log.info("Received request to delete user: {}", userId);

        return userService.deleteUser(userId)
                .then(Mono.just(ResponseEntity.ok(ApiResponse.<Void>success(Messages.USER_DELETED_SUCCESS))))
                .doOnSuccess(response -> log.info("User deletion completed successfully: {}", userId));
    }
}