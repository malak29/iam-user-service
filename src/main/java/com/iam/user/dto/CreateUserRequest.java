package com.iam.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @Email(message = "Email should be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Name is required")
    private String name;

    private String password;

    @NotNull(message = "Organization ID is required")
    private Integer orgId;

    @NotNull(message = "Department ID is required")
    private Integer departmentId;

    @NotNull(message = "User type ID is required")
    private Integer userTypeId;

    @NotNull(message = "User status ID is required")
    private Integer userStatusId;

    @NotNull(message = "Auth type ID is required")
    private Integer authTypeId;

}
