package com.alco.searchapp.entity;

import java.util.List;
import java.util.Map;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "product_index")
@Data
public class DenormalizedProductEntity {
    @Id
    private Long id;

    private Map<Long, Double> providers;

    private String ean;

    private String name;

    private List<Long> characteristicIds;

    private Long categoryId;
}
