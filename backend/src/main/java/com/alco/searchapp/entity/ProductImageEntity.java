package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("product_image")
@Data
public class ProductImageEntity {
    @Id
    private long id;

    private String path;

    @Column("product_id")
    private Long productId;

    private LocalDateTime createdAt;
}
