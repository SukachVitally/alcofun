package com.alco.searchapp.dto;

import com.alco.searchapp.constants.ProviderTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ProviderProductDto {
    private Long providerId;

    private Double price;

    private String link;

    private ProviderTypeEnum providerType;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy hh:mm:ss")
    private LocalDateTime createdAt;
}
