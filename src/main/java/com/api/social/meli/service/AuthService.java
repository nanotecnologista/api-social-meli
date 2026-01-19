package com.api.social.meli.service;

import com.api.social.meli.dto.auth.LoginRequest;
import com.api.social.meli.dto.auth.LoginResponse;
import com.api.social.meli.dto.auth.RegisterRequest;
import com.api.social.meli.dto.auth.RegisterResponse;
import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.model.mysql.UserRole;
import com.api.social.meli.repository.mysql.RoleRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    
    private static final int MAX_PASSWORD_LENGTH = 72;

    public RegisterResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("email already exists");
        }
        if (userRepository.existsByNickname(req.getNickname())) {
            throw new IllegalArgumentException("nickname already exists");
        }
        
        validatePassword(req.getPassword());

        User user = User.builder()
                .name(req.getName())
                .nickname(req.getNickname())
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .build();

        user = userRepository.save(user);

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseThrow(() -> new IllegalStateException("Role CUSTOMER not found in database"));

        if (!userRoleRepository.existsByUserAndRole(user, customerRole)) {
            UserRole ur = UserRole.builder()
                    .user(user)
                    .role(customerRole)
                    .build();
            userRoleRepository.save(ur);
        }

        return RegisterResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .roles(Set.of(RoleName.CUSTOMER.name()))
                .build();
    }

    public LoginResponse login(LoginRequest req) {
        validatePassword(req.getPassword());
        
        // login pode ser email ou nickname
        User user = userRepository.findByEmailOrNickname(req.getLogin(), req.getLogin())
                .orElseThrow(() -> new IllegalArgumentException("invalid credentials"));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("invalid credentials");
        }

        return LoginResponse.builder()
                .userId(user.getId())
                .build();
    }
    
    private void validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if (password.length() > MAX_PASSWORD_LENGTH) {
            throw new IllegalArgumentException("Password too long (max " + MAX_PASSWORD_LENGTH + " characters)");
        }
    }
}