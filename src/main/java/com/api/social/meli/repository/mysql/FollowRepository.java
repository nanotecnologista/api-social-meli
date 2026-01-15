package com.api.social.meli.repository.mysql;

import com.api.social.meli.model.mysql.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByUserIdAndSellerId(Long userId, Long sellerId);
    Optional<Follow> findByUserIdAndSellerId(Long userId, Long sellerId);
    List<Follow> findBySellerId(Long sellerId);
    List<Follow> findByUserId(Long userId);
    long countBySellerId(Long sellerId);
}
