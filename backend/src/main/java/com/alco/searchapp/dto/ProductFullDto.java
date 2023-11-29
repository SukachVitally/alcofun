package com.alco.searchapp.dto;

import com.alco.searchapp.entity.ProductSearchEntity;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductFullDto {
    private Long id;

    private String ean;

    private String name;

    private String description;

    private List<ProductSearchEntity.Provider> providers;

    private String imageUrl;

    private Long categoryId;

    private String categoryName;

    private List<ProductSearchEntity.Characteristic> characteristics;

    private RatingDto rating;
}
