package com.alco.searchapp.dto.productlist;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DenormalizedProductDto {
    private Long id;

    private Map<Long, Double> providers;

    private String ean;

    private String name;

    private List<Long> characteristicIds;

    private Long categoryId;
}
