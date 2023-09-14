package com.alco.searchapp.entity;

import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("characteristic")
public class CharacteristicEntity {
    @Id
    private Long id;

    private Integer typeId;

    private String value;

    private LocalDateTime createdAt;
}
