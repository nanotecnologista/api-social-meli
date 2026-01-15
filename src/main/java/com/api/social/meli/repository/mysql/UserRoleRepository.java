package com.api.social.meli.repository.mysql;

import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.model.mysql.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, Long> {
    boolean existsByUserAndRole(User user, Role role);
    
    @Query("SELECT ur.role.name FROM UserRole ur WHERE ur.user.id = :userId")
    Set<RoleName> findRolesByUserId(@Param("userId") Long userId);
    
    List<UserRole> findByUserId(Long userId);
}