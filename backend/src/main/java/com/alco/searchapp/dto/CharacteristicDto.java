package com.alco.searchapp.dto;

import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CharacteristicDto {
    private Long id;

    private CharacteristicTypeEnum type;

    private Object value;
}
