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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-mongo")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class AuthControllerRegisterIT {

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

    @BeforeEach
    void setUp() {
        followRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        roleRepository.save(Role.builder().name(RoleName.CUSTOMER).build());
    }

    @Test
    void register_success_createsUserAndCustomerRole() throws Exception {
        String payload = "{" +
                "\"name\":\"Joao\"," +
                "\"nickname\":\"joao123\"," +
                "\"email\":\"joao@example.com\"," +
                "\"password\":\"secret12\"" +
                "}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.nickname").value("joao123"))
                .andExpect(jsonPath("$.email").value("joao@example.com"))
                .andExpect(jsonPath("$.roles[0]").value("CUSTOMER"));

        User saved = userRepository.findByEmail("joao@example.com").orElseThrow();
        assertThat(saved.getNickname()).isEqualTo("joao123");
        assertThat(saved.getPassword()).isNotEqualTo("secret12");

        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER).orElseThrow();
        assertThat(userRoleRepository.existsByUserAndRole(saved, customerRole)).isTrue();
    }

    @Test
    void register_invalidPayload_returns400WithValidationErrors() throws Exception {
        String payload = "{" +
                "\"name\":\"\"," +
                "\"nickname\":\"\"," +
                "\"email\":\"not-an-email\"," +
                "\"password\":\"123\"" +
                "}";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.errors.name").exists())
                .andExpect(jsonPath("$.errors.nickname").exists())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }
}
