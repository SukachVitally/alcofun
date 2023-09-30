package com.alco.searchapp.repository;

import com.alco.searchapp.entity.ProviderProductEntity;
import java.time.LocalDateTime;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProviderProductRepository extends R2dbcRepository<ProviderProductEntity, Long> {
    @Query("SELECT DISTINCT ON (e.provider_id) * FROM provider_product e WHERE e.product_id = :productId " +
            "AND e.created_at > :createdAt ORDER BY e.provider_id, e.id DESC")
    Flux<ProviderProductEntity> findByProductIdAndCreatedAtAfter(Long productId, LocalDateTime createdAt);
}
