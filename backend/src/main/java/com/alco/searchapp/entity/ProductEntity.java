package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("product")
@Data
public class ProductEntity {
    @Id
    private Long id;

    private String ean;

    private String name;

    private String description;

    private Long categoryId;

    private Long providerId;

    private LocalDateTime createdAt;
}
