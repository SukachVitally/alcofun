package com.alco.searchapp.repository;

import com.alco.searchapp.entity.ProductCharacteristicEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductCharacteristicRepository extends R2dbcRepository<ProductCharacteristicEntity, Long> {
    Flux<ProductCharacteristicEntity> findAllByProductId(Long productId);
}
