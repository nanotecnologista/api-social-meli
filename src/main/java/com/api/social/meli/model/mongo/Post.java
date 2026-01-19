package com.api.social.meli.model.mongo;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(collection = "posts")
@CompoundIndex(name = "idx_posts_user_date", def = "{'userId': 1, 'date': -1}")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id
    private String id; // você pode expor isso como post_id no DTO

    @NotNull
    @Indexed
    private Long userId; // vendedor (user_id no payload)

    @NotNull
    private LocalDate date; // date no formato dd-MM-aaaa (no DTO)

    @NotNull
    private PostProduct product;

    @NotNull
    private Integer categoryId; // category no payload (id da categoria)

    @NotNull
    private String categoryName;

    @NotNull
    private BigDecimal price;

    private Boolean hasPromo;

    private BigDecimal discount;
}