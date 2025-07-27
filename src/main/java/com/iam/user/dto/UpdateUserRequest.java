package com.iam.user.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Email(message = "Email should be valid")
    private String email;

    private String username;
    private String name;
    private String password;
    private Integer orgId;
    private Integer departmentId;
    private Integer userTypeId;
    private Integer userStatusId;
    private Integer authTypeId;
}
