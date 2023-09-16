package com.alco.searchapp.dto.productlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class DenormalizedProductDto {
    private Long id;

    @JsonIgnore
    private Map<Long, Double> providers;

    private String ean;

    private String name;

    @JsonIgnore
    private List<Long> characteristicIds;

    @JsonIgnore
    private Long categoryId;
}
