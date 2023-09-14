package com.alco.searchapp.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class BrandDto {
    private Long id;

    private String name;
}
