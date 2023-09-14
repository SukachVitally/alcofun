package com.alco.searchapp.dto.productlist;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductListItemDto {
    private Long id;

    private String name;

    private String description;

    private Double price;

    private String imageUrl;
}
