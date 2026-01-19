package com.api.social.meli.controller;

import com.api.social.meli.model.mongo.Post;
import com.api.social.meli.model.mongo.PostProduct;
import com.api.social.meli.model.mysql.*;
import com.api.social.meli.repository.mongo.PostRepository;
import com.api.social.meli.repository.mysql.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test-mongo")
@DisplayName("Post Controller - Integration Tests")
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
class PostControllerIT {

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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PostRepository postRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private User customer;
    private User seller;
    private Category category;
    private Product product;

    @BeforeEach
    void setUp() {
        postRepository.deleteAll();
        followRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();
        userRoleRepository.deleteAll();
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role customerRole = roleRepository.save(Role.builder().name(RoleName.CUSTOMER).build());
        Role sellerRole = roleRepository.save(Role.builder().name(RoleName.SELLER).build());

        customer = userRepository.save(User.builder()
                .name("Customer One")
                .nickname("customer1")
                .email("customer1@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(UserRole.builder()
                .user(customer)
                .role(customerRole)
                .build());

        seller = userRepository.save(User.builder()
                .name("Seller One")
                .nickname("seller1")
                .email("seller1@example.com")
                .password(passwordEncoder.encode("password123"))
                .build());
        userRoleRepository.save(UserRole.builder()
                .user(seller)
                .role(customerRole)
                .build());
        userRoleRepository.save(UserRole.builder()
                .user(seller)
                .role(sellerRole)
                .build());

        category = categoryRepository.save(Category.builder()
                .name("Cadeiras")
                .build());

        product = productRepository.save(Product.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Red Black")
                .category(category)
                .seller(seller)
                .price(BigDecimal.valueOf(1500.50))
                .active(true)
                .build());
    }

    @Test
    @DisplayName("TI-0005: Registro de publicação - Seller publica produto com sucesso")
    void publishPost_sellerPublishesProduct_success() throws Exception {
        LocalDate today = LocalDate.now();
        String payload = String.format("{" +
                "\"user_id\":%d," +
                "\"date\":\"%s\"," +
                "\"product_id\":%d," +
                "\"category\":%d" +
                "}", seller.getId(), today.format(dateFormatter), product.getId(), category.getId());

        mockMvc.perform(post("/products/publish")
                        .header("X-User-Id", seller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        assertThat(postRepository.findAll().stream()
                .filter(p -> p.getUserId().equals(seller.getId()))
                .toList()).hasSize(1);
    }

    @Test
    @DisplayName("TI-0005: Timeline - Customer segue seller e vê publicação na timeline")
    void followedTimeline_customerFollowsSellerAndSeesPost_success() throws Exception {
        LocalDate today = LocalDate.now();

        followRepository.save(Follow.builder()
                .user(customer)
                .seller(seller)
                .build());

        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(today)
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(false)
                .build());

        mockMvc.perform(get("/products/followed/{userId}/list", customer.getId())
                        .header("X-User-Id", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(customer.getId()))
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].user_id").value(seller.getId()));
    }

    @Test
    @DisplayName("TI-0005: Timeline - Customer deixa de seguir e não vê mais publicação")
    void followedTimeline_customerUnfollowsAndDoesNotSeePost_success() throws Exception {
        LocalDate today = LocalDate.now();

        Follow follow = followRepository.save(Follow.builder()
                .user(customer)
                .seller(seller)
                .build());

        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(today)
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(false)
                .build());

        followRepository.delete(follow);

        mockMvc.perform(get("/products/followed/{userId}/list", customer.getId())
                        .header("X-User-Id", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(customer.getId()))
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts", hasSize(0)));
    }

    @Test
    @DisplayName("TI-0005: Timeline - Ordenação por data descendente (mais recente primeiro)")
    void followedTimeline_withDateDescOrder_returnsNewestFirst() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        followRepository.save(Follow.builder()
                .user(customer)
                .seller(seller)
                .build());

        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(yesterday)
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName("Old Product")
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(false)
                .build());

        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(today)
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName("New Product")
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(false)
                .build());

        mockMvc.perform(get("/products/followed/{userId}/list?order=date_desc", customer.getId())
                        .header("X-User-Id", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(2)))
                .andExpect(jsonPath("$.posts[0].product.product_name").value("New Product"))
                .andExpect(jsonPath("$.posts[1].product.product_name").value("Old Product"));
    }

    @Test
    @DisplayName("TI-0005: Timeline - Apenas posts das últimas 2 semanas aparecem")
    void followedTimeline_onlyShowsPostsFromLast14Days() throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate day13Ago = today.minusDays(13);
        LocalDate day15Ago = today.minusDays(15);

        followRepository.save(Follow.builder()
                .user(customer)
                .seller(seller)
                .build());

        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(day13Ago)
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName("Recent Product")
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(false)
                .build());

        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(day15Ago)
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName("Old Product")
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(false)
                .build());

        mockMvc.perform(get("/products/followed/{userId}/list", customer.getId())
                        .header("X-User-Id", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].product.product_name").value("Recent Product"));
    }

    @Test
    @DisplayName("TI-0007: Publicação promocional - Seller publica produto em promoção")
    void publishPromoPost_sellerPublishesPromoProduct_success() throws Exception {
        LocalDate today = LocalDate.now();
        String payload = String.format("{" +
                "\"user_id\":%d," +
                "\"date\":\"%s\"," +
                "\"product_id\":%d," +
                "\"category\":%d," +
                "\"has_promo\":true," +
                "\"discount\":0.25" +
                "}", seller.getId(), today.format(dateFormatter), product.getId(), category.getId());

        mockMvc.perform(post("/products/promo-pub")
                        .header("X-User-Id", seller.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk());

        assertThat(postRepository.findByUserIdAndHasPromoTrue(seller.getId())).hasSize(1);
    }

    @Test
    @DisplayName("TI-0007: Contagem de produtos em promoção - Retorna quantidade correta")
    void promoCount_returnsCorrectCount() throws Exception {
        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(LocalDate.now())
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(true)
                .discount(BigDecimal.valueOf(0.25))
                .build());

        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(LocalDate.now())
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(true)
                .discount(BigDecimal.valueOf(0.15))
                .build());

        mockMvc.perform(get("/products/promo-pub/count?user_id={userId}", seller.getId())
                        .header("X-User-Id", seller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(seller.getId()))
                .andExpect(jsonPath("$.user_name").value("seller1"))
                .andExpect(jsonPath("$.promo_products_count").value(2));
    }

    @Test
    @DisplayName("TI-0007: Listagem de produtos em promoção - Retorna lista correta")
    void promoList_returnsCorrectList() throws Exception {
        postRepository.save(Post.builder()
                .userId(seller.getId())
                .date(LocalDate.now())
                .product(PostProduct.builder()
                        .productId(product.getId())
                        .productName(product.getName())
                        .type(product.getType())
                        .brand(product.getBrand())
                        .color(product.getColor())
                        .build())
                .categoryId(category.getId().intValue())
                .categoryName(category.getName())
                .price(product.getPrice())
                .hasPromo(true)
                .discount(BigDecimal.valueOf(0.25))
                .build());

        mockMvc.perform(get("/products/promo-pub/list?user_id={userId}", seller.getId())
                        .header("X-User-Id", seller.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_id").value(seller.getId()))
                .andExpect(jsonPath("$.user_name").value("seller1"))
                .andExpect(jsonPath("$.posts").isArray())
                .andExpect(jsonPath("$.posts", hasSize(1)))
                .andExpect(jsonPath("$.posts[0].has_promo").value(true))
                .andExpect(jsonPath("$.posts[0].discount").value(0.25));
    }
}
