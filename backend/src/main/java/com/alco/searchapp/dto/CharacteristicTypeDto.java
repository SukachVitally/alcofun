package com.alco.searchapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CharacteristicTypeDto {
    private Integer id;

    private String name;

    @JsonProperty("uom")
    private String unitOfMeasurement;
}
