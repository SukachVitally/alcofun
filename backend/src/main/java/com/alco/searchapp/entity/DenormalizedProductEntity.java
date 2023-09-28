package com.alco.searchapp.entity;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = DenormalizedProductEntity.INDEX)
@Data
public class DenormalizedProductEntity {
    public static final String INDEX = "products";
    @Id
    private Long id;

    @Field(type = FieldType.Nested)
    private List<Provider> providers;

    @Field(type = FieldType.Keyword)
    private String ean;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Long)
    private List<Long> characteristicIds;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword, index = false)
    private String imageUrl;

    @Data
    public static class Provider {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Float)
        private Double price;
    }
}
