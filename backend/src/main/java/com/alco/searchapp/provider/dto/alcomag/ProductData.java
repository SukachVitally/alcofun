package com.alco.searchapp.provider.dto.alcomag;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ProductData {
    private String name;

    private String description;

    @JsonProperty("gtin")
    private String ean;

    private Brand brand;

    private Offers offers;

    @Data
    public static class Brand {
        private String name;
    }

    @Data
    public static class Offers {
        private Double price;
    }
}
