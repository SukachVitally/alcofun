package com.alco.searchapp.properites;

import com.alco.searchapp.constants.CategoryTypeEnum;
import lombok.Data;

@Data
public class ProviderBlock {
    private CategoryTypeEnum type;

    private String url;
}
