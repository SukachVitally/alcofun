package com.alco.searchapp.provider.dto;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Builder
@Getter
public class Product {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String ean;

    @Positive
    @NotNull
    private Double price;

    @NotNull
    private CategoryTypeEnum category;

    private String description;

    @Positive
    @NotNull
    private Integer volume;

    @NotNull
    @NotEmpty
    private String brand;

    @Range(max = 96)
    private Float abv;

    @NotNull
    @NotEmpty
    private String country;

    @NotNull
    private String link;

    private List<String> imageUrls;

    private ProviderTypeEnum providerType;
}
