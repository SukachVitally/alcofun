package com.alco.searchapp.dto;

import com.alco.searchapp.constants.CategoryTypeEnum;
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

    private List<ProductProviderDto> providers;

    private List<String> imageUrls;

    private CategoryTypeEnum category;

    private List<CharacteristicDto> characteristics;

    private RatingDto rating;
}
