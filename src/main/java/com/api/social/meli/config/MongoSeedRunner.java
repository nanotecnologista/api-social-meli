package com.api.social.meli.config;

import com.api.social.meli.model.mongo.Post;
import com.api.social.meli.model.mongo.PostProduct;
import com.api.social.meli.repository.mongo.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class MongoSeedRunner implements ApplicationRunner {

    private final PostRepository postRepository;

    @Override
    public void run(ApplicationArguments args) {
        Post post1 = Post.builder()
                .userId(1L) // seller id (do MySQL seed)
                .date(LocalDate.now().minusDays(1))
                .product(PostProduct.builder()
                        .productId(1L)
                        .productName("Cadeira Gamer")
                        .type("Gamer")
                        .brand("Racer")
                        .color("Red & Black")
                        .notes("Special Edition")
                        .build())
                .categoryId(1)
                .categoryName("Cadeiras")
                .price(new BigDecimal("1500.50"))
                .hasPromo(false)
                .build();

        Post post2 = Post.builder()
                .userId(2L) // outro seller id
                .date(LocalDate.now().minusDays(3))
                .product(PostProduct.builder()
                        .productId(2L)
                        .productName("Teclado Mecânico")
                        .type("Keyboard")
                        .brand("KeyPro")
                        .color("Black")
                        .notes("Switch blue")
                        .build())
                .categoryId(2)
                .categoryName("Teclados")
                .price(new BigDecimal("499.90"))
                .hasPromo(true)
                .discount(new BigDecimal("0.25"))
                .build();

        seedIfMissing(post1);
        seedIfMissing(post2);
    }

    private void seedIfMissing(Post post) {
        Long userId = post.getUserId();
        Long productId = post.getProduct().getProductId();

        if (postRepository.existsByUserIdAndProductProductId(userId, productId)) {
            return;
        }

        postRepository.save(post);
    }
}