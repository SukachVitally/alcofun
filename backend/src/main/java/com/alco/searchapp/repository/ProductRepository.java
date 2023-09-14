package com.alco.searchapp.repository;

import com.alco.searchapp.entity.ProductEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ProductRepository extends R2dbcRepository<ProductEntity, Long> {
    Mono<ProductEntity> findOneByEan(String ean);
}
