package com.api.social.meli.service;

import com.api.social.meli.dto.common.PaginationParams;
import com.api.social.meli.dto.user.FollowerCountResponse;
import com.api.social.meli.dto.user.FollowerListResponse;
import com.api.social.meli.dto.user.FollowedListResponse;
import com.api.social.meli.model.mysql.Follow;
import com.api.social.meli.model.mysql.RoleName;
import com.api.social.meli.model.mysql.User;
import com.api.social.meli.repository.mysql.FollowRepository;
import com.api.social.meli.repository.mysql.UserRepository;
import com.api.social.meli.repository.mysql.UserRoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FollowService - Unit Tests")
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private FollowService followService;

    private User customer;
    private User seller;

    @BeforeEach
    void setUp() {
        customer = User.builder()
                .id(1L)
                .nickname("customer1")
                .followingCount(0)
                .build();

        seller = User.builder()
                .id(2L)
                .nickname("seller1")
                .followersCount(0)
                .build();
    }

    @Test
    @DisplayName("T-0001: Verificar se o usuário a ser seguido existe - Usuário existe")
    void followUser_whenUserToFollowExists_shouldContinueNormally() {
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(userRoleRepository.findRolesByUserId(customer.getId())).thenReturn(Set.of(RoleName.CUSTOMER));
        when(userRoleRepository.findRolesByUserId(seller.getId())).thenReturn(Set.of(RoleName.SELLER));
        when(followRepository.existsByUserIdAndSellerId(customer.getId(), seller.getId())).thenReturn(false);
        when(followRepository.save(any(Follow.class))).thenReturn(Follow.builder().build());

        followService.followUser(customer.getId(), seller.getId());

        verify(followRepository).save(any(Follow.class));
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("T-0001: Verificar se o usuário a ser seguido existe - Usuário não existe")
    void followUser_whenUserToFollowDoesNotExist_shouldThrowException() {
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userRepository.findById(seller.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.followUser(customer.getId(), seller.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User to follow not found");

        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    @DisplayName("T-0001: Verificar se o usuário que está seguindo existe - Usuário não existe")
    void followUser_whenFollowerUserDoesNotExist_shouldThrowException() {
        when(userRepository.findById(customer.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.followUser(customer.getId(), seller.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User not found");

        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    @DisplayName("T-0002: Verificar se o usuário a deixar de seguir existe - Usuário existe")
    void unfollowUser_whenUserToUnfollowExists_shouldContinueNormally() {
        Follow follow = Follow.builder()
                .user(customer)
                .seller(seller)
                .build();

        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userRepository.findById(seller.getId())).thenReturn(Optional.of(seller));
        when(followRepository.findByUserIdAndSellerId(customer.getId(), seller.getId()))
                .thenReturn(Optional.of(follow));

        followService.unfollowUser(customer.getId(), seller.getId());

        verify(followRepository).delete(follow);
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    @DisplayName("T-0002: Verificar se o usuário a deixar de seguir existe - Usuário não existe")
    void unfollowUser_whenUserToUnfollowDoesNotExist_shouldThrowException() {
        when(userRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(userRepository.findById(seller.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> followService.unfollowUser(customer.getId(), seller.getId()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("User to unfollow not found");

        verify(followRepository, never()).delete(any(Follow.class));
    }

    @Test
    @DisplayName("T-0003: Validação do tipo de ordenação alfabética - name_asc válido")
    void getFollowersList_withNameAscOrder_shouldAccept() {
        User seller = User.builder().id(1L).nickname("seller1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        
        Page<Follow> emptyPage = new PageImpl<>(List.of());
        when(followRepository.findBySellerId(eq(1L), any(Pageable.class))).thenReturn(emptyPage);

        FollowerListResponse response = followService.getFollowersList(1L, "name_asc", null, null, null, null);

        assertThat(response).isNotNull();
        verify(followRepository).findBySellerId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("T-0003: Validação do tipo de ordenação alfabética - name_desc válido")
    void getFollowersList_withNameDescOrder_shouldAccept() {
        User seller = User.builder().id(1L).nickname("seller1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        
        Page<Follow> emptyPage = new PageImpl<>(List.of());
        when(followRepository.findBySellerId(eq(1L), any(Pageable.class))).thenReturn(emptyPage);

        FollowerListResponse response = followService.getFollowersList(1L, "name_desc", null, null, null, null);

        assertThat(response).isNotNull();
        verify(followRepository).findBySellerId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("T-0003: Validação do tipo de ordenação alfabética - valor inválido")
    void getFollowersList_withInvalidOrder_shouldThrowException() {
        User seller = User.builder().id(1L).nickname("seller1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));

        assertThatThrownBy(() -> followService.getFollowersList(1L, "invalid_order", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid order parameter. Use 'name_asc' or 'name_desc'");
    }

    @Test
    @DisplayName("T-0004: Verificar ordenação alfabética correta - Ordem ascendente (A-Z)")
    void getFollowersList_withNameAsc_shouldReturnSortedListAscending() {
        User seller = User.builder().id(1L).nickname("seller1").build();
        User follower1 = User.builder().id(2L).nickname("alice").build();
        User follower2 = User.builder().id(3L).nickname("bob").build();
        User follower3 = User.builder().id(4L).nickname("charlie").build();

        Follow follow1 = Follow.builder().user(follower1).seller(seller).build();
        Follow follow2 = Follow.builder().user(follower2).seller(seller).build();
        Follow follow3 = Follow.builder().user(follower3).seller(seller).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        
        Page<Follow> followsPage = new PageImpl<>(Arrays.asList(follow1, follow2, follow3));
        when(followRepository.findBySellerId(eq(1L), any(Pageable.class))).thenReturn(followsPage);

        FollowerListResponse response = followService.getFollowersList(1L, "name_asc", null, null, null, null);

        assertThat(response.getFollowers()).hasSize(3);
        assertThat(response.getFollowers().get(0).getUserName()).isEqualTo("alice");
        assertThat(response.getFollowers().get(1).getUserName()).isEqualTo("bob");
        assertThat(response.getFollowers().get(2).getUserName()).isEqualTo("charlie");
    }

    @Test
    @DisplayName("T-0004: Verificar ordenação alfabética correta - Ordem descendente (Z-A)")
    void getFollowersList_withNameDesc_shouldReturnSortedListDescending() {
        User seller = User.builder().id(1L).nickname("seller1").build();
        User follower1 = User.builder().id(2L).nickname("alice").build();
        User follower2 = User.builder().id(3L).nickname("bob").build();
        User follower3 = User.builder().id(4L).nickname("charlie").build();

        Follow follow1 = Follow.builder().user(follower3).seller(seller).build();
        Follow follow2 = Follow.builder().user(follower2).seller(seller).build();
        Follow follow3 = Follow.builder().user(follower1).seller(seller).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        
        Page<Follow> followsPage = new PageImpl<>(Arrays.asList(follow1, follow2, follow3));
        when(followRepository.findBySellerId(eq(1L), any(Pageable.class))).thenReturn(followsPage);

        FollowerListResponse response = followService.getFollowersList(1L, "name_desc", null, null, null, null);

        assertThat(response.getFollowers()).hasSize(3);
        assertThat(response.getFollowers().get(0).getUserName()).isEqualTo("charlie");
        assertThat(response.getFollowers().get(1).getUserName()).isEqualTo("bob");
        assertThat(response.getFollowers().get(2).getUserName()).isEqualTo("alice");
    }

    @Test
    @DisplayName("T-0007: Verificar cálculo exato de quantidade de seguidores")
    void getFollowersCount_shouldReturnExactCount() {
        User seller = User.builder().id(1L).nickname("seller1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(followRepository.countBySellerId(1L)).thenReturn(5L);

        FollowerCountResponse response = followService.getFollowersCount(1L);

        assertThat(response).isNotNull();
        assertThat(response.getUserId()).isEqualTo(1L);
        assertThat(response.getUserName()).isEqualTo("seller1");
        assertThat(response.getFollowersCount()).isEqualTo(5L);
        verify(followRepository).countBySellerId(1L);
    }

    @Test
    @DisplayName("T-0007: Verificar cálculo de seguidores quando não há seguidores")
    void getFollowersCount_whenNoFollowers_shouldReturnZero() {
        User seller = User.builder().id(1L).nickname("seller1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(seller));
        when(followRepository.countBySellerId(1L)).thenReturn(0L);

        FollowerCountResponse response = followService.getFollowersCount(1L);

        assertThat(response.getFollowersCount()).isEqualTo(0L);
    }

    @Test
    @DisplayName("T-0003: Validação do tipo de ordenação alfabética para followed list - name_asc válido")
    void getFollowedList_withNameAscOrder_shouldAccept() {
        User customer = User.builder().id(1L).nickname("customer1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        
        Page<Follow> emptyPage = new PageImpl<>(List.of());
        when(followRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(emptyPage);

        FollowedListResponse response = followService.getFollowedList(1L, "name_asc", null, null, null, null);

        assertThat(response).isNotNull();
        verify(followRepository).findByUserId(eq(1L), any(Pageable.class));
    }

    @Test
    @DisplayName("T-0003: Validação do tipo de ordenação alfabética para followed list - valor inválido")
    void getFollowedList_withInvalidOrder_shouldThrowException() {
        User customer = User.builder().id(1L).nickname("customer1").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));

        assertThatThrownBy(() -> followService.getFollowedList(1L, "invalid_order", null, null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid order parameter. Use 'name_asc' or 'name_desc'");
    }

    @Test
    @DisplayName("T-0004: Verificar ordenação alfabética correta para followed list - Ordem ascendente")
    void getFollowedList_withNameAsc_shouldReturnSortedListAscending() {
        User customer = User.builder().id(1L).nickname("customer1").build();
        User seller1 = User.builder().id(2L).nickname("alice_seller").build();
        User seller2 = User.builder().id(3L).nickname("bob_seller").build();

        Follow follow1 = Follow.builder().user(customer).seller(seller1).build();
        Follow follow2 = Follow.builder().user(customer).seller(seller2).build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(customer));
        
        Page<Follow> followsPage = new PageImpl<>(Arrays.asList(follow1, follow2));
        when(followRepository.findByUserId(eq(1L), any(Pageable.class))).thenReturn(followsPage);

        FollowedListResponse response = followService.getFollowedList(1L, "name_asc", null, null, null, null);

        assertThat(response.getFollowed()).hasSize(2);
        assertThat(response.getFollowed().get(0).getUserName()).isEqualTo("alice_seller");
        assertThat(response.getFollowed().get(1).getUserName()).isEqualTo("bob_seller");
    }
}
