package com.api.social.meli.repository.mysql;

import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName name);
    Optional<Role> findById(Long id);
    boolean existsByName(RoleName name);
    boolean existsById(Long id);
}