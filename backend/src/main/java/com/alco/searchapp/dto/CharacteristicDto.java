package com.alco.searchapp.dto;

import com.alco.searchapp.constants.CharacteristicTypeEnum;
import com.alco.searchapp.serializer.CharacteristicTypeEnumSerializer;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class CharacteristicDto {
    private Long id;

    @JsonSerialize(using = CharacteristicTypeEnumSerializer.class)
    private CharacteristicTypeEnum type;

    private Object value;
}
