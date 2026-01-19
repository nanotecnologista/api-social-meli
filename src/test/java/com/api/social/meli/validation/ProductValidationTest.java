package com.api.social.meli.validation;

import com.api.social.meli.dto.product.ProductCreateRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Product Validation - Unit Tests")
class ProductValidationTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validação: product_name não pode estar vazio")
    void productName_whenEmpty_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    @DisplayName("Validação: product_name não pode ser null")
    void productName_whenNull_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(null)
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
    }

    @Test
    @DisplayName("Validação: product_name máximo 40 caracteres")
    void productName_whenExceeds40Characters_shouldFailValidation() {
        String longName = "a".repeat(41);
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(longName)
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> 
            v.getPropertyPath().toString().equals("name") && 
            v.getMessage().contains("40")
        );
    }

    @Test
    @DisplayName("Validação: product_name com 40 caracteres deve passar")
    void productName_with40Characters_shouldPassValidation() {
        String validName = "a".repeat(40);
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name(validName)
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("name")).isEmpty();
    }

    @Test
    @DisplayName("Validação: product_name não pode conter caracteres especiais (%, &, $)")
    void productName_withSpecialCharacters_shouldFailValidation() {
        String[] invalidNames = {"Product%Name", "Product&Name", "Product$Name", "Product@Name"};

        for (String invalidName : invalidNames) {
            ProductCreateRequest request = ProductCreateRequest.builder()
                    .name(invalidName)
                    .type("Gamer")
                    .brand("Razer")
                    .color("Black")
                    .categoryId(100L)
                    .price(BigDecimal.valueOf(1500.50))
                    .build();

            Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

            assertThat(violations).isNotEmpty();
            assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("name"));
        }
    }

    @Test
    @DisplayName("Validação: product_name permite espaços")
    void productName_withSpaces_shouldPassValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer Racer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("name")).isEmpty();
    }

    @Test
    @DisplayName("Validação: type não pode estar vazio")
    void type_whenEmpty_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("type"));
    }

    @Test
    @DisplayName("Validação: type máximo 15 caracteres")
    void type_whenExceeds15Characters_shouldFailValidation() {
        String longType = "a".repeat(16);
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type(longType)
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("type"));
    }

    @Test
    @DisplayName("Validação: type não pode conter caracteres especiais")
    void type_withSpecialCharacters_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer%Type")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("type"));
    }

    @Test
    @DisplayName("Validação: brand não pode estar vazio")
    void brand_whenEmpty_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("brand"));
    }

    @Test
    @DisplayName("Validação: brand máximo 25 caracteres")
    void brand_whenExceeds25Characters_shouldFailValidation() {
        String longBrand = "a".repeat(26);
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand(longBrand)
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("brand"));
    }

    @Test
    @DisplayName("Validação: brand não pode conter caracteres especiais")
    void brand_withSpecialCharacters_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer&Co")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("brand"));
    }

    @Test
    @DisplayName("Validação: color não pode estar vazio")
    void color_whenEmpty_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    @DisplayName("Validação: color máximo 15 caracteres")
    void color_whenExceeds15Characters_shouldFailValidation() {
        String longColor = "a".repeat(16);
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color(longColor)
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    @DisplayName("Validação: color não pode conter caracteres especiais")
    void color_withSpecialCharacters_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Red&Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("color"));
    }

    @Test
    @DisplayName("Validação: price não pode ser null")
    void price_whenNull_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(null)
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    @DisplayName("Validação: price máximo 10.000.000")
    void price_whenExceedsMaximum_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(10000000.01))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    @DisplayName("Validação: price com valor 10.000.000 deve passar")
    void price_withMaximumValue_shouldPassValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(10000000.00))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).filteredOn(v -> v.getPropertyPath().toString().equals("price")).isEmpty();
    }

    @Test
    @DisplayName("Validação: price deve ser positivo")
    void price_whenZeroOrNegative_shouldFailValidation() {
        ProductCreateRequest request1 = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.ZERO)
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations1 = validator.validate(request1);
        assertThat(violations1).isNotEmpty();
        assertThat(violations1).anyMatch(v -> v.getPropertyPath().toString().equals("price"));

        ProductCreateRequest request2 = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(-100))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations2 = validator.validate(request2);
        assertThat(violations2).isNotEmpty();
        assertThat(violations2).anyMatch(v -> v.getPropertyPath().toString().equals("price"));
    }

    @Test
    @DisplayName("Validação: categoryId não pode ser null")
    void categoryId_whenNull_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(null)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("categoryId"));
    }

    @Test
    @DisplayName("Validação: categoryId deve ser positivo")
    void categoryId_whenZeroOrNegative_shouldFailValidation() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Black")
                .categoryId(0L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isNotEmpty();
        assertThat(violations).anyMatch(v -> v.getPropertyPath().toString().equals("categoryId"));
    }

    @Test
    @DisplayName("Validação: Produto válido completo deve passar em todas as validações")
    void validProduct_shouldPassAllValidations() {
        ProductCreateRequest request = ProductCreateRequest.builder()
                .name("Cadeira Gamer")
                .type("Gamer")
                .brand("Razer")
                .color("Red Black")
                .categoryId(100L)
                .price(BigDecimal.valueOf(1500.50))
                .build();

        Set<ConstraintViolation<ProductCreateRequest>> violations = validator.validate(request);

        assertThat(violations).isEmpty();
    }
}
