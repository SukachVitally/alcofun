package com.alco.searchapp.repository;

import com.alco.searchapp.entity.ProductProviderEntity;
import java.time.LocalDateTime;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

public interface ProductProviderRepository extends R2dbcRepository<ProductProviderEntity, Long> {
    @Query("SELECT DISTINCT ON (e.provider_id) * FROM product_provider e WHERE e.product_id = :productId " +
            "AND e.created_at > :createdAt ORDER BY e.provider_id, e.id DESC")
    Flux<ProductProviderEntity> findByProductIdAndCreatedAtAfter(Long productId, LocalDateTime createdAt);

    Flux<ProductProviderEntity> findAllByProductIdOrderById(Long productId);
}
