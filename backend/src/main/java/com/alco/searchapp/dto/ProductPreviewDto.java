package com.alco.searchapp.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductPreviewDto {
    private Long id;

    private String ean;

    private String name;

    private Double price;

    private String imageUrl;

    private RatingDto rating;

    private Boolean hot;

    private Integer percent;
}
