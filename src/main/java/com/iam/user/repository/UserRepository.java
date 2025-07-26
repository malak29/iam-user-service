package com.iam.user.repository;

import com.iam.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    List<User> findUsersByOrgId(Integer orgId);

    List<User> findUsersByDepartmentId(Integer departmentId);

    List<User> findUsersByOrgIdAndDepartmentId(Integer orgId, Integer departmentId);

    List<User> findUsersByUserStatusId(Integer userStatusId);

    @Query("SELECT u FROM User u WHERE u.orgId = :orgId AND u.userStatusId = 1")
    List<User> findActiveUsersByOrgId(@Param("orgId") Integer orgId);

    List<User> findUsersByUserTypeId(Integer userTypeId);

}
