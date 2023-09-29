package com.alco.searchapp.dto.productlist;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductInfoDto {
    private Long id;

    private String ean;

    private String name;

    private List<Long> characteristicIds;

    private Long categoryId;

    private Double price;

    private String imageUrl;
}
