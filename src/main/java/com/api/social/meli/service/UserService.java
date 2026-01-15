package com.api.social.meli.service;

import com.api.social.meli.dto.user.ActivateSellerResponse;
import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.model.mysql.UserRole;
import com.api.social.meli.repository.mysql.RoleRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;

    public ActivateSellerResponse activateSeller(Long authUserId, Long userIdToActivate) {
        if (!authUserId.equals(userIdToActivate)) {
            throw new IllegalArgumentException("you can only activate seller for yourself");
        }


        User user = userRepository.findById(userIdToActivate)
                .orElseThrow(() -> new IllegalArgumentException("user not found"));

        Role sellerRole = roleRepository.findByName(RoleName.SELLER)
                .orElseThrow(() -> new IllegalStateException("Role SELLER not found in database"));


        // adiciona SELLER
        if (!userRoleRepository.existsByUserAndRole(user, sellerRole)) {
            userRoleRepository.save(UserRole.builder().user(user).role(sellerRole).build());
        }

        return ActivateSellerResponse.builder()
                .userId(user.getId())
                .roles(Set.of(RoleName.CUSTOMER.name(), RoleName.SELLER.name()))
                .build();
    }
}