package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("country")
@Data
public class CountryEntity {
    @Id
    private Long id;

    private String name;

    private LocalDateTime createdAt;
}
