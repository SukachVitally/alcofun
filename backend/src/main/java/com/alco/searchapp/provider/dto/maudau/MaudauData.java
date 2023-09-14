package com.alco.searchapp.provider.dto.maudau;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class MaudauData {
    private Product product;

    @Data
    public static class Product {
        private Current current;
    }

    @Data
    public static class Current {
        private String barcode;

        private String name;

        private String description;

        @JsonProperty("final_price")
        private Double price;

        private String image;

        @JsonProperty("product_brand_label")
        private String brand;

        @JsonProperty("country_of_origin_label")
        private String country;


    }
}
