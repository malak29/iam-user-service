package com.iam.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID userId;
    private String email;
    private String username;
    private String name;
    private Integer orgId;
    private Integer departmentId;
    private Integer authTypeId;
    private Integer userTypeId;
    private Integer userStatusId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UserResponse(com.iam.user.model.User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.name = user.getName();
        this.orgId = user.getOrgId();
        this.departmentId = user.getDepartmentId();
        this.authTypeId = user.getAuthTypeId();
        this.userTypeId = user.getUserTypeId();
        this.userStatusId = user.getUserStatusId();
        this.createdAt = user.getCreatedAt();
        this.updatedAt = user.getUpdatedAt();
    }

}
