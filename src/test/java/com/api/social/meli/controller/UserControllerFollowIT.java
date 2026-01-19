package com.api.social.meli.controller;

import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.RoleRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-mongo")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class UserControllerFollowIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRoleRepository userRoleRepository;

    @Autowired
    private FollowRepository followRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private User customer1;
    private User customer2;
    private User seller1;

    @BeforeEach
    void setUp() {
        followRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role customerRole = roleRepository.save(Role.builder().name(RoleName.CUSTOMER).build());
        Role sellerRole = roleRepository.save(Role.builder().name(RoleName.SELLER).build());

        customer1 = userRepository.save(User.builder()
                .name("Customer One")
                .nickname("customer1")
                .email("customer1@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(customer1)
                .role(customerRole)
                .build());

        customer2 = userRepository.save(User.builder()
                .name("Customer Two")
                .nickname("customer2")
                .email("customer2@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(customer2)
                .role(customerRole)
                .build());

        seller1 = userRepository.save(User.builder()
                .name("Seller One")
                .nickname("seller1")
                .email("seller1@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(seller1)
                .role(customerRole)
                .build());

        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(seller1)
                .role(sellerRole)
                .build());
    }

    @Test
    void followUser_sellerCannotFollowCustomerOnly_returns400() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", seller1.getId(), customer1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void followUser_sellerCanFollowSeller_returns200() throws Exception {
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER).orElseThrow();
        Role sellerRole = roleRepository.findByName(RoleName.SELLER).orElseThrow();

        User anotherSeller = userRepository.save(User.builder()
                .name("Another Seller")
                .nickname("seller2")
                .email("seller2_unique@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(anotherSeller)
                .role(customerRole)
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(anotherSeller)
                .role(sellerRole)
                .build());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", seller1.getId(), anotherSeller.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk());
    }

    @Test
    void followUser_success_createsFollowRelationship() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        assertThat(followRepository.existsByUserIdAndSellerId(customer1.getId(), seller1.getId())).isTrue();

        User updatedSeller = userRepository.findById(seller1.getId()).orElseThrow();
        assertThat(updatedSeller.getFollowersCount()).isEqualTo(1);

        User updatedCustomer = userRepository.findById(customer1.getId()).orElseThrow();
        assertThat(updatedCustomer.getFollowingCount()).isEqualTo(1);
    }

    @Test
    void followUser_userNotFound_returns400() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), 99999L)
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void followUser_alreadyFollowing_returns400() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void followUser_cannotFollowSelf_returns400() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void unfollowUser_success_removesFollowRelationship() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/unfollow/{userIdToUnfollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        assertThat(followRepository.existsByUserIdAndSellerId(customer1.getId(), seller1.getId())).isFalse();

        User updatedSeller = userRepository.findById(seller1.getId()).orElseThrow();
        assertThat(updatedSeller.getFollowersCount()).isEqualTo(0);

        User updatedCustomer = userRepository.findById(customer1.getId()).orElseThrow();
        assertThat(updatedCustomer.getFollowingCount()).isEqualTo(0);
    }

    @Test
    void unfollowUser_notFollowing_returns400() throws Exception {
        mockMvc.perform(post("/users/{userId}/unfollow/{userIdToUnfollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFollowersCount_success_returnsCorrectCount() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/count", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(seller1.getId()))
                .andExpect(jsonPath("$.userName").value("seller1"))
                .andExpect(jsonPath("$.followersCount").value(2));
    }

    @Test
    void getFollowersList_success_returnsFollowersList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/list", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(seller1.getId()))
                .andExpect(jsonPath("$.userName").value("seller1"))
                .andExpect(jsonPath("$.followers").isArray())
                .andExpect(jsonPath("$.followers.length()").value(2));
    }

    @Test
    void getFollowersList_withNameAscOrder_returnsSortedList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/list?order=name_asc", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].userName").value("customer1"))
                .andExpect(jsonPath("$.followers[1].userName").value("customer2"));
    }

    @Test
    void getFollowersList_withNameDescOrder_returnsSortedList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/list?order=name_desc", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers[0].userName").value("customer2"))
                .andExpect(jsonPath("$.followers[1].userName").value("customer1"));
    }

    @Test
    void getFollowersList_withInvalidOrder_returns400() throws Exception {
        mockMvc.perform(get("/users/{userId}/followers/list?order=invalid", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFollowedList_success_returnsFollowedList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followed/list", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(customer1.getId()))
                .andExpect(jsonPath("$.user_name").value("customer1"))
                .andExpect(jsonPath("$.followed").isArray())
                .andExpect(jsonPath("$.followed.length()").value(1))
                .andExpect(jsonPath("$.followed[0].userId").value(seller1.getId()))
                .andExpect(jsonPath("$.followed[0].userName").value("seller1"));
    }

    @Test
    void getFollowedList_withNameAscOrder_returnsSortedList() throws Exception {
        User seller2 = userRepository.save(User.builder()
                .name("Seller Two")
                .nickname("aseller")
                .email("seller2@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller2.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followed/list?order=name_asc", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followed[0].userName").value("aseller"))
                .andExpect(jsonPath("$.followed[1].userName").value("seller1"));
    }

    @Test
    void getFollowedList_withNameDescOrder_returnsSortedList() throws Exception {
        User seller2 = userRepository.save(User.builder()
                .name("Seller Two")
                .nickname("aseller")
                .email("seller2@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller2.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followed/list?order=name_desc", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followed[0].userName").value("seller1"))
                .andExpect(jsonPath("$.followed[1].userName").value("aseller"));
    }
}
