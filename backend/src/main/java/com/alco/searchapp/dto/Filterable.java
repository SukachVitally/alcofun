package com.alco.searchapp.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Filterable {
    @Builder.Default
    private List<Long> categories = List.of();

    @Builder.Default
    private List<Long> providers = List.of();

    @Builder.Default
    private Price price = new Price(null, null);

    private String search;

    @Builder.Default
    private List<Long> characteristics = List.of();

    @Builder.Default
    private Boolean hot = false;

    public record Price(Double min, Double max) {}
}
