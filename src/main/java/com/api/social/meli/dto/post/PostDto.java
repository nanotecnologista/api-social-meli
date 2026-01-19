package com.api.social.meli.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("post_id")
    private String postId;

    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate date;

    private PostProductDto product;

    private Integer category;

    private BigDecimal price;

    @JsonProperty("has_promo")
    private Boolean hasPromo;

    private BigDecimal discount;
}
