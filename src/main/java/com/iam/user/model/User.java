package com.iam.user.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "userid")
    private UUID userId;

    @Email
    @NotBlank
    @Column(unique = true, nullable = false)
    private String email;

    @NotBlank
    @Column(unique = true, nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String name;

    @Column(name = "hashedpassword")
    private String hashedPassword;

    @Column(name ="orgid", nullable = false)
    private Integer orgId;

    @Column(name = "departmentid", nullable = false)
    private Integer departmentId;

    @Column(name = "usertypeid", nullable = false)
    private Integer userTypeId;

    @Column(name = "userstatusid", nullable = false)
    private Integer userStatusId;

    @Column(name = "authtypeid", nullable = false)
    private Integer authTypeId;

    @CreationTimestamp
    @Column(name = "createdat")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedat")
    private LocalDateTime updatedAt;
}
