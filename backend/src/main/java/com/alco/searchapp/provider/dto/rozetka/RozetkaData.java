package com.alco.searchapp.provider.dto.rozetka;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.r2dbc.spi.Parameter;
import java.util.List;
import lombok.Data;

@Data
public class RozetkaData {
    private List<String> image;

    private String name;

    @JsonProperty("gtin13")
    private String ean;

    private Offer offers;

    private String description;

    private Double price;

    private Brand brand;

    private AggregateRating aggregateRating;

    @Data
    public static class Brand {
        private String name;
    }

    @Data
    public static class Offer {
        private Double price;
    }

    @Data
    public static class AggregateRating {
        private Float ratingValue;

        private Integer ratingCount;
    }
}
