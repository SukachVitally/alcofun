package com.alco.searchapp.entity;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.InnerField;
import org.springframework.data.elasticsearch.annotations.MultiField;

@Document(indexName = "#{@environment.getProperty('app.product.search.index')}")
@Data
public class ProductSearchEntity {
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    @Field(type = FieldType.Object)
    private List<Provider> providers;

    @Field(type = FieldType.Keyword)
    private String ean;

    @MultiField(mainField = @Field(type = FieldType.Text),
            otherFields = { @InnerField(suffix = "keyword", type = FieldType.Keyword, ignoreAbove = 50) })
    private String name;

    @Field(type = FieldType.Text, index = false)
    private String description;

    @Field(type = FieldType.Object)
    private List<Characteristic> characteristics;

    @Field(type = FieldType.Long)
    private Long categoryId;

    @Field(type = FieldType.Keyword, index = false)
    private String categoryName;

    @Field(type = FieldType.Keyword, index = false)
    private List<String> imageUrls;

    @Field(type = FieldType.Float)
    private Float compositeRating;

    @Field(type = FieldType.Float, index = false)
    private Integer votes;

    @Field(type = FieldType.Float)
    private Float internalRating;

    @Field(type = FieldType.Float)
    private Double price;

    @Field(type = FieldType.Boolean)
    private Boolean hot;

    @Field(type = FieldType.Float)
    private Double percent;

    @Getter
    @Builder
    public static class Provider {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Float, index = false)
        private Double price;

        @Field(type = FieldType.Keyword, index = false)
        private String name;

        @Field(type = FieldType.Keyword, index = false)
        private String link;

        @Field(type = FieldType.Boolean, index = false)
        private Boolean hot;
    }

    @Getter
    @Builder
    public static class Category {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Keyword, index = false)
        private String name;
    }

    @Getter
    @Builder
    public static class Characteristic {
        @Field(type = FieldType.Long)
        private Long id;

        @Field(type = FieldType.Keyword, index = false)
        private String name;

        @Field(type = FieldType.Keyword, index = false)
        private Object value;

        @Field(type = FieldType.Keyword, index = false)
        private String unitOfMeasurement;
    }
}
