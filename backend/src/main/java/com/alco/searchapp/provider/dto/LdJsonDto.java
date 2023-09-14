package com.alco.searchapp.provider.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LdJsonDto {
    @JsonProperty("@context")
    private String context;

    @JsonProperty("@type")
    private String type;
}
