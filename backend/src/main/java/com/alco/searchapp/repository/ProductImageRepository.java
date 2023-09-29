package com.alco.searchapp.repository;

import com.alco.searchapp.entity.ProductImageEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductImageRepository extends R2dbcRepository<ProductImageEntity, Long> {
    Flux<ProductImageEntity> findByProductId(long productId);
    Mono<ProductImageEntity> findOneByProductIdOrderByIdAsc(long productId);
}
