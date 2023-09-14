package com.alco.searchapp.dto;

import com.alco.searchapp.constants.ProviderTypeEnum;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProviderDto {
    private Long id;

    private ProviderTypeEnum type;
}
