package com.alco.searchapp.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductSearchDto {
    private Long id;
    private Map<String, Double> providers;
    private String ean;
    private String name;
    private List<Long> characteristicIds;
    private Long categoryId;
}
