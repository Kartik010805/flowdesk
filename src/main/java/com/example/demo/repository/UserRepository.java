
package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.enums.Role;
import com.example.demo.enums.EmploymentStatus;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    List<User> findByDepartmentId(Long departmentId);

    @EntityGraph(attributePaths = {"department", "manager"})
    List<User> findAllByOrderByIdAsc();

    List<User> findByRoleAndStatusOrderByFullNameAsc(
            Role role,
            EmploymentStatus status
    );
}
