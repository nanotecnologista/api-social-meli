package com.api.social.meli.repository.mongo;

import com.api.social.meli.model.mysql.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByUserIdAndSellerId(Long userId, Long sellerId);
    Optional<Follow> findByUserIdAndSellerId(Long userId, Long sellerId);
    List<Follow> findBySellerIdOrderByUserNicknameAsc(Long sellerId);
    List<Follow> findBySellerIdOrderByUserNicknameDesc(Long sellerId);
    List<Follow> findByUserIdOrderBySellerNicknameAsc(Long userId);
    List<Follow> findByUserIdOrderBySellerNicknameDesc(Long userId);
    long countBySellerId(Long sellerId);
}