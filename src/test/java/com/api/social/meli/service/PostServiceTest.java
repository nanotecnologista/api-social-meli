package com.api.social.meli.service;

import com.api.social.meli.dto.post.PostDto;
import com.api.social.meli.dto.post.TimelineResponse;
import com.api.social.meli.model.mongo.Post;
import com.api.social.meli.model.mongo.PostProduct;
import com.api.social.meli.model.mysql.Follow;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mongo.PostRepository;
import com.api.social.meli.repository.mysql.CategoryRepository;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.ProductRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PostService - Unit Tests")
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private FollowRepository followRepository;

    @InjectMocks
    private PostService postService;

    private User customer;
    private User seller;

    @BeforeEach
    void setUp() {
        customer = User.builder()
                .id(1L)
                .nickname("customer1")
                .build();

        seller = User.builder()
                .id(2L)
                .nickname("seller1")
                .build();
    }

    @Test
    @DisplayName("T-0005: Validação do tipo de ordenação por data - date_asc válido")
    void getFollowedTimeline_withDateAscOrder_shouldAccept() {
        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of());

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), "date_asc");

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(customer.getId());
        verify(userRepository).existsById(customer.getId());
    }

    @Test
    @DisplayName("T-0005: Validação do tipo de ordenação por data - date_desc válido")
    void getFollowedTimeline_withDateDescOrder_shouldAccept() {
        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of());

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), "date_desc");

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(customer.getId());
        verify(userRepository).existsById(customer.getId());
    }

    @Test
    @DisplayName("T-0005: Validação do tipo de ordenação por data - valor inválido")
    void getFollowedTimeline_withInvalidOrder_shouldThrowException() {
        Follow follow = Follow.builder().user(customer).seller(seller).build();
        
        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of(follow));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(anyList(), any(LocalDate.class)))
                .thenReturn(List.of());

        assertThatThrownBy(() -> postService.getFollowedTimeline(customer.getId(), "invalid_order"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid order parameter. Use 'date_asc' or 'date_desc'");
    }

    @Test
    @DisplayName("T-0006: Verificar ordenação por data correta - Ordem ascendente (mais antigo para mais recente)")
    void getFollowedTimeline_withDateAsc_shouldReturnSortedListAscending() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);

        Follow follow = Follow.builder().user(customer).seller(seller).build();

        Post post1 = createPost("post1", seller.getId(), twoDaysAgo);
        Post post2 = createPost("post2", seller.getId(), yesterday);
        Post post3 = createPost("post3", seller.getId(), today);

        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of(follow));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(anyList(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(post1, post2, post3));

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), "date_asc");

        assertThat(response.getPosts()).hasSize(3);
        assertThat(response.getPosts().get(0).getDate()).isEqualTo(twoDaysAgo);
        assertThat(response.getPosts().get(1).getDate()).isEqualTo(yesterday);
        assertThat(response.getPosts().get(2).getDate()).isEqualTo(today);
    }

    @Test
    @DisplayName("T-0006: Verificar ordenação por data correta - Ordem descendente (mais recente para mais antigo)")
    void getFollowedTimeline_withDateDesc_shouldReturnSortedListDescending() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate twoDaysAgo = today.minusDays(2);

        Follow follow = Follow.builder().user(customer).seller(seller).build();

        Post post1 = createPost("post1", seller.getId(), twoDaysAgo);
        Post post2 = createPost("post2", seller.getId(), yesterday);
        Post post3 = createPost("post3", seller.getId(), today);

        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of(follow));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(anyList(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(post1, post2, post3));

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), "date_desc");

        assertThat(response.getPosts()).hasSize(3);
        assertThat(response.getPosts().get(0).getDate()).isEqualTo(today);
        assertThat(response.getPosts().get(1).getDate()).isEqualTo(yesterday);
        assertThat(response.getPosts().get(2).getDate()).isEqualTo(twoDaysAgo);
    }

    @Test
    @DisplayName("T-0006: Verificar ordenação por data padrão - Sem parâmetro order (deve ser desc)")
    void getFollowedTimeline_withoutOrder_shouldReturnDescendingByDefault() {
        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        Follow follow = Follow.builder().user(customer).seller(seller).build();

        Post post1 = createPost("post1", seller.getId(), yesterday);
        Post post2 = createPost("post2", seller.getId(), today);

        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of(follow));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(anyList(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(post1, post2));

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), null);

        assertThat(response.getPosts()).hasSize(2);
        assertThat(response.getPosts().get(0).getDate()).isEqualTo(today);
        assertThat(response.getPosts().get(1).getDate()).isEqualTo(yesterday);
    }

    @Test
    @DisplayName("T-0008: Verificar filtro de 14 dias - Apenas posts das últimas 2 semanas")
    void getFollowedTimeline_shouldReturnOnlyPostsFromLast14Days() {
        LocalDate today = LocalDate.now();
        LocalDate day5Ago = today.minusDays(5);
        LocalDate day13Ago = today.minusDays(13);
        LocalDate day14Ago = today.minusDays(14);
        LocalDate day15Ago = today.minusDays(15);
        LocalDate day20Ago = today.minusDays(20);

        Follow follow = Follow.builder().user(customer).seller(seller).build();

        Post validPost1 = createPost("post1", seller.getId(), day5Ago);
        Post validPost2 = createPost("post2", seller.getId(), day13Ago);
        Post validPost3 = createPost("post3", seller.getId(), day14Ago);

        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of(follow));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(
                eq(List.of(seller.getId())), 
                eq(today.minusDays(14))))
                .thenReturn(Arrays.asList(validPost1, validPost2, validPost3));

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), "date_desc");

        assertThat(response.getPosts()).hasSize(3);
        verify(postRepository).findByUserIdInAndDateGreaterThanEqual(
                eq(List.of(seller.getId())), 
                eq(today.minusDays(14)));
    }

    @Test
    @DisplayName("T-0008: Verificar filtro de 14 dias - Posts antigos não devem aparecer")
    void getFollowedTimeline_shouldNotReturnPostsOlderThan14Days() {
        LocalDate today = LocalDate.now();
        LocalDate day15Ago = today.minusDays(15);

        Follow follow = Follow.builder().user(customer).seller(seller).build();

        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of(follow));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(anyList(), any(LocalDate.class)))
                .thenReturn(List.of());

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), null);

        assertThat(response.getPosts()).isEmpty();
        verify(postRepository).findByUserIdInAndDateGreaterThanEqual(
                anyList(), 
                eq(today.minusDays(14)));
    }

    @Test
    @DisplayName("T-0008: Verificar filtro de 14 dias - Post de hoje deve aparecer")
    void getFollowedTimeline_shouldIncludePostsFromToday() {
        LocalDate today = LocalDate.now();

        Follow follow = Follow.builder().user(customer).seller(seller).build();
        Post todayPost = createPost("post1", seller.getId(), today);

        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(List.of(follow));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(anyList(), any(LocalDate.class)))
                .thenReturn(List.of(todayPost));

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), null);

        assertThat(response.getPosts()).hasSize(1);
        assertThat(response.getPosts().get(0).getDate()).isEqualTo(today);
    }

    @Test
    @DisplayName("T-0008: Verificar filtro de 14 dias - Múltiplos vendedores seguidos")
    void getFollowedTimeline_withMultipleFollowedSellers_shouldReturnAllPostsWithin14Days() {
        LocalDate today = LocalDate.now();
        LocalDate day10Ago = today.minusDays(10);

        User seller2 = User.builder().id(3L).nickname("seller2").build();

        Follow follow1 = Follow.builder().user(customer).seller(seller).build();
        Follow follow2 = Follow.builder().user(customer).seller(seller2).build();

        Post post1 = createPost("post1", seller.getId(), day10Ago);
        Post post2 = createPost("post2", seller2.getId(), today);

        when(userRepository.existsById(customer.getId())).thenReturn(true);
        when(followRepository.findByUserId(customer.getId())).thenReturn(Arrays.asList(follow1, follow2));
        when(postRepository.findByUserIdInAndDateGreaterThanEqual(anyList(), any(LocalDate.class)))
                .thenReturn(Arrays.asList(post1, post2));

        TimelineResponse response = postService.getFollowedTimeline(customer.getId(), null);

        assertThat(response.getPosts()).hasSize(2);
        verify(postRepository).findByUserIdInAndDateGreaterThanEqual(
                eq(Arrays.asList(seller.getId(), seller2.getId())), 
                eq(today.minusDays(14)));
    }

    private Post createPost(String postId, Long userId, LocalDate date) {
        return Post.builder()
                .id(postId)
                .userId(userId)
                .date(date)
                .product(PostProduct.builder()
                        .productId(1L)
                        .productName("Test Product")
                        .type("Test Type")
                        .brand("Test Brand")
                        .color("Test Color")
                        .build())
                .categoryId(100)
                .categoryName("Test Category")
                .price(BigDecimal.valueOf(100.00))
                .hasPromo(false)
                .build();
    }
}
