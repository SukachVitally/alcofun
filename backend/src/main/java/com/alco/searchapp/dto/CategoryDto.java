package com.alco.searchapp.dto;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.serializer.CategoryTypeEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryDto {
    private Long id;

    @JsonSerialize(using = CategoryTypeEnumSerializer.class)
    private CategoryTypeEnum type;
}
