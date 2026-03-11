package com.sanyark.leavemanagement.repository;

import com.sanyark.leavemanagement.entity.User;
import com.sanyark.leavemanagement.enums.Gender;
import com.sanyark.leavemanagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    long countByRole(Role role);

    Optional<User> findByEmail(String email);

    Optional<User> findByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    boolean existsByEmployeeCode(String employeeCode);

    List<User> findByRole(Role role);

    List<User> findByGender(Gender gender);

    List<User> findByRoleAndGender(Role role, Gender gender);
}