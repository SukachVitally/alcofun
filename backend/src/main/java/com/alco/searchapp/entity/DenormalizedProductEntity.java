package com.alco.searchapp.entity;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "products")
@Data
public class DenormalizedProductEntity {
    @Id
    private Long id;

    private Map<Long, Double> providers;

    @Field(type = FieldType.Keyword)
    private String ean;

    @Field(type = FieldType.Keyword)
    private String name;

    @Field(type = FieldType.Long)
    private List<Long> characteristicIds;

    @Field(type = FieldType.Long)
    private Long categoryId;
}
