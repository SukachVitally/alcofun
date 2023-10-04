package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("best_price")
public class BestPriceEntity {
    @Id
    private Long id;

    private Long productId;

    private Long providerId;

    private Double price;

    private Boolean hot;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
