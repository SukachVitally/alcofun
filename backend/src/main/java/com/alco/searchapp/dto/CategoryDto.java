package com.alco.searchapp.dto;

import com.alco.searchapp.constants.CategoryTypeEnum;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CategoryDto {
    private Long id;

    private CategoryTypeEnum type;
}
