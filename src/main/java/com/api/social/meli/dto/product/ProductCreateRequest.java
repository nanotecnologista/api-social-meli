package com.api.social.meli.dto.product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateRequest {

    @NotBlank
    @Size(max = 40)
    @Pattern(regexp = "^[\\p{L}0-9 ]+$")
    private String name;

    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^[\\p{L}0-9 ]+$")
    private String type;

    @NotBlank
    @Size(max = 25)
    @Pattern(regexp = "^[\\p{L}0-9 ]+$")
    private String brand;

    @NotBlank
    @Size(max = 15)
    @Pattern(regexp = "^[\\p{L}0-9 ]+$")
    private String color;

    @NotNull
    @Positive
    private Long categoryId;

    @NotNull
    @DecimalMin("0.01")
    @DecimalMax("10000000.00")
    private BigDecimal price;
}
