package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("provider_product")
@Data
public class ProviderProductEntity {
    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

    private Double price;

    private String link;

    @Column("provider_id")
    private Long providerId;

    private LocalDateTime createdAt;
}
