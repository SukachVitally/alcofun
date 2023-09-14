package com.alco.searchapp.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductInfoDto {
    private Long id;

    private String name;

    private Double price;

    private String imageUrl;
}
