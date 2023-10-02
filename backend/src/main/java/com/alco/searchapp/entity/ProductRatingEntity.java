package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("product_rating")
@Data
public class ProductRatingEntity {
    @Id
    private Long id;

    private Long productId;

    private Long providerId;

    private Float rating;

    private Integer voteCount;

    private LocalDateTime createdAt;
}
