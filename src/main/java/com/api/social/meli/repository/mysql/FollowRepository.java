package com.api.social.meli.repository.mysql;

import com.api.social.meli.model.mysql.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByUserIdAndSellerId(Long userId, Long sellerId);
    Optional<Follow> findByUserIdAndSellerId(Long userId, Long sellerId);
    List<Follow> findBySellerId(Long sellerId);
    List<Follow> findByUserId(Long userId);

    Page<Follow> findBySellerId(Long sellerId, Pageable pageable);
    Page<Follow> findByUserId(Long userId, Pageable pageable);

    long countBySellerId(Long sellerId);
    long countByUserId(Long userId);

    @Query("SELECT f.seller.id FROM Follow f WHERE f.user.id = :userId")
    List<Long> findSellerIdsByUserId(@Param("userId") Long userId);
}
