package com.api.social.meli.repository.mongo;

import com.api.social.meli.model.mongo.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    boolean existsByUserIdAndProductProductId(Long userId, Long productId);

    long countByUserId(Long userId);

    long countByUserIdAndHasPromoTrue(Long userId);

    List<Post> findByUserIdAndHasPromoTrue(Long userId);

    Page<Post> findByUserId(Long userId, Pageable pageable);

    long countByUserIdAndDateGreaterThanEqual(Long userId, LocalDate date);

    Page<Post> findByUserIdAndDateGreaterThanEqual(Long userId, LocalDate date, Pageable pageable);

    List<Post> findByUserIdInAndDateGreaterThanEqual(List<Long> userIds, LocalDate date);

}