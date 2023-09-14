package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("product_characteristic")
public class ProductCharacteristicEntity {
    @Id
    private Long id;

    private Long characteristicId;

    private Long productId;

    private LocalDateTime createdAt;
}
