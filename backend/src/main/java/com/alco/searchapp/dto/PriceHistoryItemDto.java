package com.alco.searchapp.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class PriceHistoryItemDto {
    private Double price;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
}
