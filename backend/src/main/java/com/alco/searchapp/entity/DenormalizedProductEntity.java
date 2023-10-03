package com.alco.searchapp.entity;

import com.alco.searchapp.dto.RatingDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Document(indexName = DenormalizedProductEntity.INDEX)
@Data
public class DenormalizedProductEntity {
    public static final String INDEX = "products";
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Nested)
    private List<Provider> providers;

    @Field(type = FieldType.Keyword)
    private String ean;

    @MultiField(mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 50) })
    private String name;

    @Field(type = FieldType.Long)
    private List<Long> characteristicIds;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword, index = false)
    private String imageUrl;

    @Field(type = FieldType.Object, index = false)
    private RatingDto rating;

    @Field(type = FieldType.Float)
    private Float internalRating;

    @Data
    @AllArgsConstructor
    public static class Provider {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Float)
        private Double price;
    }
}
