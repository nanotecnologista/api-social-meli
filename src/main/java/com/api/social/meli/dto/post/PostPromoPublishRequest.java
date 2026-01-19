package com.api.social.meli.dto.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PostPromoPublishRequest extends PostPublishRequest {

    @NotNull
    @JsonProperty("has_promo")
    private Boolean hasPromo;

    @JsonProperty("discount")
    @DecimalMin("0.01")
    @DecimalMax("0.99")
    private BigDecimal discount;

    @AssertTrue
    public boolean isPromoValid() {
        if (hasPromo == null) {
            return false;
        }
        if (!hasPromo) {
            return discount == null;
        }
        return discount != null;
    }
}
