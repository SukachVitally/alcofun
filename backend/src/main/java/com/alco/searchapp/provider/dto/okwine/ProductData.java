package com.alco.searchapp.provider.dto.okwine;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Data;

@Data
public class ProductData {
    private Props props;

    @Data
    public static class Props {
        private PageProps pageProps;
    }

    @Data
    public static class PageProps {
        private HydrationData hydrationData;
    }

    @Data
    public static class HydrationData {
        private ProductPageStore productPageStore;
    }

    @Data
    public static class ProductPageStore {
        private Product product;
    }

    @Data
    public static class Product {
        private Meta meta;

        private List<String> images;

        private List<String> barcodes;

        private Prices prices;

        private List<Characteristic> characteristics;
    }

    @Data
    public static class Prices {
        private Double price;
    }

    @Data
    public static class Meta {
        private String name;

        private String description;
    }

    @Data
    public static class Characteristic {
        @JsonProperty("value")
        private CharacteristicKey key;


        private List<CharacteristicValue> values;

        @Data
        public static class CharacteristicKey {
            @JsonProperty("ua")
            private String name;
        }

        @Data
        public static class CharacteristicValue {
            private CharacteristicValueObject value;

            @Data
            public static class CharacteristicValueObject {
                private String ua;
            }
        }
    }
}
