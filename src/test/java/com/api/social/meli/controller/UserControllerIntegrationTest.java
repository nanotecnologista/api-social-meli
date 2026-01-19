package com.api.social.meli.controller;

import com.api.social.meli.model.mysql.Follow;
import com.api.social.meli.model.mysql.Role;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.RoleRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-mongo")
@DisplayName("User Controller - Integration Tests (Complete Flow)")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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
    private User customer3;
    private User seller1;
    private User seller2;

    @BeforeEach
    void setUp() {
        followRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role customerRole = roleRepository.save(Role.builder().name(RoleName.CUSTOMER).build());
        Role sellerRole = roleRepository.save(Role.builder().name(RoleName.SELLER).build());

        customer1 = userRepository.save(User.builder()
                .name("Alice Customer")
                .nickname("alice")
                .email("alice@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(customer1)
                .role(customerRole)
                .build());

        customer2 = userRepository.save(User.builder()
                .name("Bob Customer")
                .nickname("bob")
                .email("bob@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(customer2)
                .role(customerRole)
                .build());

        customer3 = userRepository.save(User.builder()
                .name("Charlie Customer")
                .nickname("charlie")
                .email("charlie@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(customer3)
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

        seller2 = userRepository.save(User.builder()
                .name("Seller Two")
                .nickname("aseller")
                .email("seller2@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(seller2)
                .role(customerRole)
                .build());
        userRoleRepository.save(com.api.social.meli.model.mysql.UserRole.builder()
                .user(seller2)
                .role(sellerRole)
                .build());
    }

    @Test
    @DisplayName("TI-0001: Fluxo completo de Follow - Customer segue seller com sucesso")
    void followFlow_customerFollowsSeller_success() throws Exception {
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
    @DisplayName("TI-0002: Fluxo de contagem de seguidores - Múltiplos customers seguem seller")
    void followersCountFlow_multipleCustomersFollowSeller_returnsCorrectCount() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer3.getId(), seller1.getId())
                        .header("X-User-Id", customer3.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/count", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(seller1.getId()))
                .andExpect(jsonPath("$.userName").value("seller1"))
                .andExpect(jsonPath("$.followersCount").value(3));
    }

    @Test
    @DisplayName("TI-0003: Listagem de seguidores com ordenação - Ordem alfabética ascendente")
    void followersListFlow_withNameAscOrder_returnsSortedList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer3.getId(), seller1.getId())
                        .header("X-User-Id", customer3.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/list?order=name_asc", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(seller1.getId()))
                .andExpect(jsonPath("$.userName").value("seller1"))
                .andExpect(jsonPath("$.followers").isArray())
                .andExpect(jsonPath("$.followers", hasSize(3)))
                .andExpect(jsonPath("$.followers[0].userName").value("alice"))
                .andExpect(jsonPath("$.followers[1].userName").value("bob"))
                .andExpect(jsonPath("$.followers[2].userName").value("charlie"));
    }

    @Test
    @DisplayName("TI-0003: Listagem de seguidores com ordenação - Ordem alfabética descendente")
    void followersListFlow_withNameDescOrder_returnsSortedList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer3.getId(), seller1.getId())
                        .header("X-User-Id", customer3.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/list?order=name_desc", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers").isArray())
                .andExpect(jsonPath("$.followers", hasSize(3)))
                .andExpect(jsonPath("$.followers[0].userName").value("charlie"))
                .andExpect(jsonPath("$.followers[1].userName").value("bob"))
                .andExpect(jsonPath("$.followers[2].userName").value("alice"));
    }

    @Test
    @DisplayName("TI-0004: Listagem de seguidos com ordenação - Ordem alfabética ascendente")
    void followedListFlow_withNameAscOrder_returnsSortedList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller2.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followed/list?order=name_asc", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(customer1.getId()))
                .andExpect(jsonPath("$.userName").value("alice"))
                .andExpect(jsonPath("$.followed").isArray())
                .andExpect(jsonPath("$.followed", hasSize(2)))
                .andExpect(jsonPath("$.followed[0].userName").value("aseller"))
                .andExpect(jsonPath("$.followed[1].userName").value("seller1"));
    }

    @Test
    @DisplayName("TI-0004: Listagem de seguidos com ordenação - Ordem alfabética descendente")
    void followedListFlow_withNameDescOrder_returnsSortedList() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller2.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followed/list?order=name_desc", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followed").isArray())
                .andExpect(jsonPath("$.followed", hasSize(2)))
                .andExpect(jsonPath("$.followed[0].userName").value("seller1"))
                .andExpect(jsonPath("$.followed[1].userName").value("aseller"));
    }

    @Test
    @DisplayName("TI-0006: Fluxo completo de Unfollow - Customer deixa de seguir seller")
    void unfollowFlow_customerUnfollowsSeller_success() throws Exception {
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
    @DisplayName("TI-0006: Fluxo Unfollow - Verificar lista de seguidores após unfollow")
    void unfollowFlow_checkFollowersListAfterUnfollow_listUpdated() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer2.getId(), seller1.getId())
                        .header("X-User-Id", customer2.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/list", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers", hasSize(2)));

        mockMvc.perform(post("/users/{userId}/unfollow/{userIdToUnfollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followers/list", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followers", hasSize(1)))
                .andExpect(jsonPath("$.followers[0].userName").value("bob"));
    }

    @Test
    @DisplayName("TI-0001 + TI-0006: Fluxo completo - Follow -> Verificar lista -> Unfollow -> Verificar lista")
    void completeFollowUnfollowFlow_success() throws Exception {
        mockMvc.perform(post("/users/{userId}/follow/{userIdToFollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followed/list", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followed", hasSize(1)))
                .andExpect(jsonPath("$.followed[0].userId").value(seller1.getId()));

        mockMvc.perform(post("/users/{userId}/unfollow/{userIdToUnfollow}", customer1.getId(), seller1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/users/{userId}/followed/list", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.followed", hasSize(0)));
    }

    @Test
    @DisplayName("TI-0003: Validação de ordenação inválida - Deve retornar erro 400")
    void followersListFlow_withInvalidOrder_returns400() throws Exception {
        mockMvc.perform(get("/users/{userId}/followers/list?order=invalid_order", seller1.getId())
                        .header("X-User-Id", seller1.getId()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("TI-0004: Validação de ordenação inválida para followed - Deve retornar erro 400")
    void followedListFlow_withInvalidOrder_returns400() throws Exception {
        mockMvc.perform(get("/users/{userId}/followed/list?order=invalid_order", customer1.getId())
                        .header("X-User-Id", customer1.getId()))
                .andExpect(status().isBadRequest());
    }
}
