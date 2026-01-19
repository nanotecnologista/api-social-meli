package com.api.social.meli.model.mongo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostProduct {

    @NotNull
    private Long productId;

    @NotBlank
    @Size(max = 40)
    private String productName;

    @NotBlank
    @Size(max = 15)
    private String type;

    @NotBlank
    @Size(max = 25)
    private String brand;

    @NotBlank
    @Size(max = 15)
    private String color;

    @Size(max = 80)
    private String notes;
}