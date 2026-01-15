package com.api.social.meli.repository.mysql;

import com.api.social.meli.model.mysql.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByNickname(String nickname);

    Optional<User> findByEmailOrNickname(String email, String nickname);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}