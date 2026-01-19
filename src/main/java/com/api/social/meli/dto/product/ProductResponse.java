package com.api.social.meli.dto.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String type;
    private String brand;
    private String color;
    private Long categoryId;
    private String categoryName;
    private BigDecimal price;
    private Boolean active;
    private Long sellerId;
}
