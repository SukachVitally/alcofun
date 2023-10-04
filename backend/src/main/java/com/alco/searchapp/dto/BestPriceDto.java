package com.alco.searchapp.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BestPriceDto {
    private Double price;

    private Boolean hot;
}
