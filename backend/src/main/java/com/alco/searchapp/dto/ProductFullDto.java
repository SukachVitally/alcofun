package com.alco.searchapp.dto;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.serializer.CategoryTypeEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    @JsonSerialize(using = CategoryTypeEnumSerializer.class)
    private CategoryTypeEnum category;

    private List<CharacteristicDto> characteristics;

    private RatingDto rating;
}
