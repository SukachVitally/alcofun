package com.alco.searchapp.dto;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.alco.searchapp.serializer.ProviderTypeEnumSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProviderDto {
    private Long id;

    @JsonSerialize(using = ProviderTypeEnumSerializer.class)
    private ProviderTypeEnum type;
}
