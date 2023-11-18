package com.alco.searchapp.provider.dto;

import com.alco.searchapp.validation.ValidDescription;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.validator.constraints.Range;

@Builder
@Getter
public class ProductInfo {
    @NotNull
    @NotEmpty
    private String name;

    @NotNull
    @NotEmpty
    private String ean;

    @Positive
    @NotNull
    private Double price;

    @ValidDescription
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

    private List<String> imageUrls;

    private Float rating;

    private Integer votes;
}
