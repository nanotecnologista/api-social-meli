package com.api.social.meli.repository.mysql;

import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.model.mysql.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserAndRole(User user, Role role);
}