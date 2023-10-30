package com.alco.searchapp.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PriceRangeDto {
    private Double min;

    private Double max;
}
