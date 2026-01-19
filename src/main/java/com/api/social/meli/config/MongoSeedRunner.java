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
import java.util.ArrayList;
import java.util.List;

@Component
@Profile("dev")
@RequiredArgsConstructor
public class MongoSeedRunner implements ApplicationRunner {

    private final PostRepository postRepository;

    @Override
    public void run(ApplicationArguments args) {
        List<Post> posts = new ArrayList<>();
        
        // Seller João Popular (ID ~6) - Muitos posts, variados em data
        // Posts ATIVOS (últimos 14 dias)
        posts.add(createPost(6L, 1L, "Cadeira Gamer Pro", "Gamer", "Racer", "Red & Black", 
                1, "Cadeiras", "1500.50", true, "0.15", LocalDate.now().minusDays(2)));
        posts.add(createPost(6L, 2L, "Teclado Mecânico RGB", "Mecânico", "HyperX", "Black", 
                2, "Teclados", "599.90", true, "0.10", LocalDate.now().minusDays(5)));
        posts.add(createPost(6L, 3L, "Monitor 27 4K", "4K", "LG", "Black", 
                3, "Monitores", "2199.00", false, null, LocalDate.now().minusDays(8)));
        posts.add(createPost(6L, 4L, "Mouse Gamer", "Gamer", "Logitech", "Black", 
                4, "Mouses", "299.90", true, "0.20", LocalDate.now().minusDays(12)));
        
        // Posts ANTIGOS (mais de 14 dias)
        posts.add(createPost(6L, 1L, "Cadeira Gamer Pro OLD", "Gamer", "Racer", "Red & Black", 
                1, "Cadeiras", "1500.50", false, null, LocalDate.now().minusDays(20)));
        posts.add(createPost(6L, 2L, "Teclado Mecânico RGB OLD", "Mecânico", "HyperX", "Black", 
                2, "Teclados", "599.90", false, null, LocalDate.now().minusDays(30)));
        posts.add(createPost(6L, 3L, "Monitor 27 4K OLD", "4K", "LG", "Black", 
                3, "Monitores", "2199.00", false, null, LocalDate.now().minusDays(45)));
        
        // Seller Maria Famosa (ID ~7) - Vários posts
        // Posts ATIVOS
        posts.add(createPost(7L, 5L, "Headset Wireless", "Wireless", "Sony", "White", 
                5, "Headsets", "899.00", true, "0.15", LocalDate.now().minusDays(1)));
        posts.add(createPost(7L, 6L, "Webcam Full HD", "Full HD", "Logitech", "Black", 
                6, "Webcams", "499.90", false, null, LocalDate.now().minusDays(7)));
        posts.add(createPost(7L, 7L, "Notebook Gamer", "Gamer", "Acer", "Black", 
                7, "Notebooks", "5999.00", true, "0.10", LocalDate.now().minusDays(10)));
        
        // Posts ANTIGOS
        posts.add(createPost(7L, 5L, "Headset Wireless OLD", "Wireless", "Sony", "White", 
                5, "Headsets", "899.00", false, null, LocalDate.now().minusDays(25)));
        posts.add(createPost(7L, 6L, "Webcam Full HD OLD", "Full HD", "Logitech", "Black", 
                6, "Webcams", "499.90", false, null, LocalDate.now().minusDays(40)));
        
        // Seller Pedro Top (ID ~8) - Alguns posts
        // Posts ATIVOS
        posts.add(createPost(8L, 8L, "Smartphone Pro", "Pro", "Samsung", "Blue", 
                8, "Smartphones", "3499.00", true, "0.12", LocalDate.now().minusDays(3)));
        posts.add(createPost(8L, 9L, "Cadeira Ergonômica", "Ergonômica", "Herman Miller", "Gray", 
                1, "Cadeiras", "3500.00", false, null, LocalDate.now().minusDays(9)));
        
        // Posts ANTIGOS
        posts.add(createPost(8L, 8L, "Smartphone Pro OLD", "Pro", "Samsung", "Blue", 
                8, "Smartphones", "3499.00", false, null, LocalDate.now().minusDays(50)));
        
        // Seller Lucas Iniciante (ID ~9) - Poucos posts, todos recentes
        posts.add(createPost(9L, 10L, "Mouse Pad", "Extended", "Generic", "Black", 
                4, "Mouses", "49.90", false, null, LocalDate.now().minusDays(4)));
        
        // Seller Sofia Nova (ID ~10) - Poucos posts
        posts.add(createPost(10L, 11L, "Teclado Básico", "Membrana", "Multilaser", "White", 
                2, "Teclados", "89.90", true, "0.05", LocalDate.now().minusDays(6)));
        posts.add(createPost(10L, 11L, "Teclado Básico OLD", "Membrana", "Multilaser", "White", 
                2, "Teclados", "89.90", false, null, LocalDate.now().minusDays(35)));
        
        // Seller Rafael Sem Posts (ID ~11) - NENHUM POST (conforme solicitado)
        
        // Seller Camila Sozinha (ID ~12) - Tem posts mas sem seguidores
        posts.add(createPost(12L, 12L, "Smartphone Básico", "Básico", "Motorola", "Black", 
                8, "Smartphones", "899.00", false, null, LocalDate.now().minusDays(11)));
        posts.add(createPost(12L, 12L, "Smartphone Básico OLD", "Básico", "Motorola", "Black", 
                8, "Smartphones", "899.00", false, null, LocalDate.now().minusDays(60)));
        
        // Dual Roberto (ID ~13) - Posts ativos e antigos
        posts.add(createPost(13L, 13L, "Monitor Ultrawide", "Ultrawide", "Samsung", "Black", 
                3, "Monitores", "2799.00", true, "0.08", LocalDate.now().minusDays(2)));
        posts.add(createPost(13L, 13L, "Monitor Ultrawide OLD", "Ultrawide", "Samsung", "Black", 
                3, "Monitores", "2799.00", false, null, LocalDate.now().minusDays(28)));
        
        // Dual Juliana (ID ~14) - Posts variados
        posts.add(createPost(14L, 14L, "Headset Gamer", "Gamer", "Razer", "Green", 
                5, "Headsets", "799.00", true, "0.18", LocalDate.now().minusDays(5)));
        posts.add(createPost(14L, 14L, "Headset Gamer OLD", "Gamer", "Razer", "Green", 
                5, "Headsets", "799.00", false, null, LocalDate.now().minusDays(22)));
        
        // Dual Fernando (ID ~15) - Posts recentes
        posts.add(createPost(15L, 15L, "Webcam 4K", "4K", "Razer", "Black", 
                6, "Webcams", "1299.00", false, null, LocalDate.now().minusDays(13)));
        
        // Dual Patricia (ID ~16) - Sem posts ainda
        
        // Salvar todos os posts
        for (Post post : posts) {
            seedIfMissing(post);
        }
    }

    private Post createPost(Long userId, Long productId, String productName, String type, 
                           String brand, String color, Integer categoryId, String categoryName,
                           String price, Boolean hasPromo, String discount, LocalDate date) {
        Post.PostBuilder builder = Post.builder()
                .userId(userId)
                .date(date)
                .product(PostProduct.builder()
                        .productId(productId)
                        .productName(productName)
                        .type(type)
                        .brand(brand)
                        .color(color)
                        .build())
                .categoryId(categoryId)
                .categoryName(categoryName)
                .price(new BigDecimal(price))
                .hasPromo(hasPromo);
        
        if (discount != null) {
            builder.discount(new BigDecimal(discount));
        }
        
        return builder.build();
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