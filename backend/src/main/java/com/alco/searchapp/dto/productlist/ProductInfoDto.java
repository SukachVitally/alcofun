package com.alco.searchapp.dto.productlist;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProductInfoDto {
    private Long id;

    private String ean;

    private String name;

    private List<Long> characteristicIds;

    private Long categoryId;

    private Double price;
}
