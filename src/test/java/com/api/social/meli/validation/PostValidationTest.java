package com.api.social.meli.validation;

import com.api.social.meli.dto.post.PostPromoPublishRequest;
import com.api.social.meli.dto.post.PostPublishRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Post Validation - Unit Tests")
class PostValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validação: user_id não pode ser null")
    void userId_whenNull_shouldFailValidation() {
        PostPublishRequest request = PostPublishRequest.builder()
                .userId(null)
                .date(LocalDate.now())
                .productId(1L)
                .categoryId(100L)
                .build();

        Set<ConstraintViolation<PostPublishRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("userId"));
    }

    @Test
    @DisplayName("Validação: date não pode ser null")
    void date_whenNull_shouldFailValidation() {
        PostPublishRequest request = PostPublishRequest.builder()
                .userId(1L)
                .date(null)
                .productId(1L)
                .categoryId(100L)
                .build();

        Set<ConstraintViolation<PostPublishRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("date"));
    }

    @Test
    @DisplayName("Validação: product_id não pode ser null")
    void productId_whenNull_shouldFailValidation() {
        PostPublishRequest request = PostPublishRequest.builder()
                .userId(1L)
                .date(LocalDate.now())
                .productId(null)
                .categoryId(100L)
                .build();

        Set<ConstraintViolation<PostPublishRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("productId"));
    }

    @Test
    @DisplayName("Validação: category não pode ser null")
    void categoryId_whenNull_shouldFailValidation() {
        PostPublishRequest request = PostPublishRequest.builder()
                .userId(1L)
                .date(LocalDate.now())
                .productId(1L)
                .categoryId(null)
                .build();

        Set<ConstraintViolation<PostPublishRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("categoryId"));
    }

    @Test
    @DisplayName("Validação: Post válido deve passar em todas as validações")
    void validPost_shouldPassAllValidations() {
        PostPublishRequest request = PostPublishRequest.builder()
                .userId(1L)
                .date(LocalDate.now())
                .productId(1L)
                .categoryId(100L)
                .build();

        Set<ConstraintViolation<PostPublishRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Validação: Post promocional válido deve passar em todas as validações")
    void validPromoPost_shouldPassAllValidations() {
        PostPromoPublishRequest request = PostPromoPublishRequest.builder()
                .userId(1L)
                .date(LocalDate.now())
                .productId(1L)
                .categoryId(100L)
                .hasPromo(true)
                .discount(BigDecimal.valueOf(0.25))
                .build();

        Set<ConstraintViolation<PostPromoPublishRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }

    @Test
    @DisplayName("Validação: Post com data válida deve passar")
    void post_withValidDate_shouldPassValidation() {
        PostPublishRequest request = PostPublishRequest.builder()
                .userId(1L)
                .date(LocalDate.of(2021, 4, 29))
                .productId(1L)
                .categoryId(100L)
                .build();

        Set<ConstraintViolation<PostPublishRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
