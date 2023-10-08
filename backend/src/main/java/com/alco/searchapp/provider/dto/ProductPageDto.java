package com.alco.searchapp.provider.dto;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
@Data
public class ProductPageDto {
    private String url;

    private ProviderTypeEnum provider;

    private CategoryTypeEnum category;
}
