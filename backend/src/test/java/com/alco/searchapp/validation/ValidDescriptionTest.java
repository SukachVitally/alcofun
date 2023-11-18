package com.alco.searchapp.validation;

import com.alco.searchapp.BaseFunctionalTest;
import com.alco.searchapp.provider.dto.ProductInfo;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest()
public class ValidDescriptionTest extends BaseFunctionalTest {
    @Autowired
    private Validator validator;

    @Test
    public void validateValidDescription() {
        var constraints = validator.validate(create("Valid description etc ...."));
        Assertions.assertEquals(0, constraints.size());
    }

    @Test
    public void validateInvalidDescriptionAlcomag() {
        var constraints = validator.validate(create("Some value with АлкоМаг ...."));
        Assertions.assertEquals(1, constraints.size());
    }

    @Test
    public void validateInvalidDescriptionWithInternetShop() {
        var constraints = validator.validate(create("Some value with інтернет-магазин ...."));
        Assertions.assertEquals(1, constraints.size());
    }

    private ProductInfo create(String description) {
        return ProductInfo.builder()
                .ean("123")
                .name("some name")
                .description(description)
                .country("Ukraine")
                .brand("Some brand")
                .volume(100)
                .votes(0)
                .abv(40f)
                .price(100d)
                .build();
    }
}
