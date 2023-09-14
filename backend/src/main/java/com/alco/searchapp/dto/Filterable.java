package com.alco.searchapp.dto;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class Filterable {
    private List<Long> categories;

    private List<Long> providers;

    private Price price;

    private String search;

    private List<Long> characteristics;

    public record Price(Double min, Double max) {}
}
