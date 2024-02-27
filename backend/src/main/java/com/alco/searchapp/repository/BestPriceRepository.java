package com.alco.searchapp.repository;

import com.alco.searchapp.entity.BestPriceEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface BestPriceRepository extends R2dbcRepository<BestPriceEntity, Long> {
    Mono<BestPriceEntity> findFirstByProductIdOrderByIdDesc(Long productId);
}
