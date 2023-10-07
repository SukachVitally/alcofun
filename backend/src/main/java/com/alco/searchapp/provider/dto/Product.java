package com.alco.searchapp.provider.dto;

import com.alco.searchapp.constants.CategoryTypeEnum;
import com.alco.searchapp.constants.ProviderTypeEnum;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Product {
    private CategoryTypeEnum category;

    private ProviderTypeEnum providerType;

    private String url;

    private ProductInfo info;
}
