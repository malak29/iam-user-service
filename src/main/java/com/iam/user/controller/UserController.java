package com.iam.user.controller;

import com.iam.common.response.ApiResponse;
import com.iam.user.config.ApiRoutes;
import com.iam.user.config.Messages;
import com.iam.user.dto.CreateUserRequest;
import com.iam.user.dto.UserResponse;
import com.iam.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiRoutes.USERS)
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser (@Valid @RequestBody CreateUserRequest request) {
        log.info("Creating user with email: {}", request.getEmail());
        UserResponse userResponse = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userResponse, "User created successfully"));
    }

    @GetMapping(ApiRoutes.USER_BY_ID)
    public ResponseEntity<ApiResponse<UserResponse>> getUserById (@PathVariable UUID userId) {
        log.debug("Received request to get user by ID: {}", userId);

        UserResponse userResponse = userService.getUserById(userId);

        log.debug("User retrieval by ID completed Successfully: {}", userId);
        return  ResponseEntity.ok(ApiResponse.success(userResponse, Messages.USER_RETRIEVED_SUCCESS));
    }

    @GetMapping(ApiRoutes.USERS_BY_ORGANIZATION)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByOrganization(@PathVariable Integer orgId) {
        log.debug("Received request to get users by organization: {}", orgId);

        List<UserResponse> users = userService.getUsersByOrgId(orgId);

        log.debug("Users retrieval by organization completed. Found {} users for org: {}", users.size(), orgId);
        return ResponseEntity.ok(ApiResponse.success(users, Messages.USERS_RETRIEVED_SUCCESS));
    }

    @GetMapping(ApiRoutes.USERS_BY_DEPARTMENT)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getUsersByDepartment(@PathVariable Integer departmentId) {
        log.debug("Received request to get users by department: {}", departmentId);

        List<UserResponse> users = userService.getUsersByDepartment(departmentId);

        log.debug("Users retrieval by department completed. Found {} users for dept: {}", users.size(), departmentId);
        return ResponseEntity.ok(ApiResponse.success(users, Messages.USERS_RETRIEVED_SUCCESS));
    }

    @PutMapping(ApiRoutes.USER_BY_ID)
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
            @PathVariable UUID userId,
            @Valid @RequestBody CreateUserRequest request) {
        log.info("Received request to update user: {}", userId);

        UserResponse userResponse = userService.updateUser(userId, request);

        log.info("User update completed successfully: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(userResponse, Messages.USER_UPDATED_SUCCESS));
    }

    @DeleteMapping(ApiRoutes.USER_BY_ID)
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable UUID userId) {
        log.info("Received request to delete user: {}", userId);

        userService.deleteUser(userId);

        log.info("User deletion completed successfully: {}", userId);
        return ResponseEntity.ok(ApiResponse.success(Messages.USER_DELETED_SUCCESS));
    }

}
