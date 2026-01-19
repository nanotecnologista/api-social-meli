package com.api.social.meli.repository.mongo;

import com.api.social.meli.model.mongo.Post;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface PostRepository extends MongoRepository<Post, String> {

    boolean existsByUserIdAndProductProductId(Long userId, Long productId);

    long countByUserIdAndHasPromoTrue(Long userId);

    List<Post> findByUserIdAndHasPromoTrue(Long userId);

    List<Post> findByUserIdInAndDateGreaterThanEqual(List<Long> userIds, LocalDate date);

}