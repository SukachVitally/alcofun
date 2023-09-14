package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("provider")
@Data
public class ProviderEntity {
    @Id
    private Long id;

    private Integer typeId;

    private LocalDateTime createdAt;
}
